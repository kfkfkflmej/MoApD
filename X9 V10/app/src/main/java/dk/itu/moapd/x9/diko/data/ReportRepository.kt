package dk.itu.moapd.x9.diko.data

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Query
import com.google.firebase.database.Transaction
import dk.itu.moapd.x9.diko.model.Report
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import dk.itu.moapd.x9.diko.ui.main.report.CREATED_AT_KEY
import java.text.SimpleDateFormat
import java.util.Locale


class ReportRepository (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val root: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val storage: StorageRepository = StorageRepository()
) {
    /**
     *  Based on Fabricio's examples.
     *  After that I changed it according to the design of my application.
     *  When I started implementing URI and URL save I used some AI assistance that optimised my code.
     *  Earlier versions of the git repo have my original work.
     *
     *  The repository acts as a bridge between the app and the RealtimeDatabase and Storage
     *  by safely headlining access to report data and image storage.
     */
    companion object {
        private const val PATH_REPORTS = "reports"
        private const val PATH_PEOPLE = "users"

        private const val CHILD_SORTABLE_DATE = "sortableDate"
        private const val CHILD_LOCATION_KEY = "locationKey"
    }

    fun currentUserId(): String? = auth.currentUser?.uid

    private fun generateLocationKey(longitude: Double, latitude: Double): String {
        return "${longitude}_${latitude}"
    }

    fun addReport(userId: String, report: Report, now: Long = System.currentTimeMillis()) {
        // Adds a new report to the database
        val key = root.reference
            .child(PATH_REPORTS)
            .push()
            .key ?: return

        val locationKey = generateLocationKey(report.longitude, report.latitude)
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sortableDate=outputFormat.format(inputFormat.parse(report.date) ?: "")

        if (report.imageRef.isNullOrEmpty()) {
            val reportInfo = report.copy(
                userId = userId,
                locationKey = locationKey,
                sortableDate = sortableDate,
                createdAt = now,
                updatedAt = now
            )
            saveReport(key, userId, reportInfo)
        } else {
            val localUri = report.imageRef.toUri()
            val remotePath = "/${userId}/${key}.jpg"
            storage.uploadFile(localUri, remotePath)
                .addOnSuccessListener { downloadUri ->
                    val reportInfo = report.copy(
                        userId = userId,
                        locationKey = locationKey,
                        sortableDate = sortableDate,
                        imageRef = downloadUri.toString(),
                        createdAt = now,
                        updatedAt = now
                    )
                    saveReport(key, userId, reportInfo)
                }
                .addOnFailureListener { e ->
                    Log.e("ReportRepository", "Failed to upload image: ${e.message}")
                    // Fallback: save without image if upload fails
                    val reportInfo = report.copy(
                        userId = userId,
                        locationKey = locationKey,
                        sortableDate = sortableDate,
                        imageRef = null,
                        createdAt = now,
                        updatedAt = now
                    )
                    saveReport(key, userId, reportInfo)
                }
        }
    }

    private fun saveReport(key: String, userId: String, report: Report) {
        // Saves a report to the database. Created by AI
        root.reference
            .child(PATH_REPORTS)
            .child(key)
            .setValue(report)

        val userReportsRef = root.reference
            .child(PATH_PEOPLE)
            .child(userId)
            .child("numReports")

        userReportsRef.runTransaction(object : Transaction.Handler {
            // Written by AI
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentValue = currentData.getValue(Int::class.java)
                currentData.value = (currentValue ?: 0) + 1
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (error != null) {
                    Log.e("Firebase", "Failed to update count", error.toException())
                }
            }
        })
    }

    fun updateReport(userId: String, key: String, report: Report,
                     createdAt: Long?, now: Long = System.currentTimeMillis()) {
        // Updates an existing report in the database.
        val locationKey = generateLocationKey(report.longitude, report.latitude)
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sortableDate=outputFormat.format(inputFormat.parse(report.date) ?: "")

        if (report.imageRef != null && !report.imageRef.startsWith("http")) {
            val localUri = report.imageRef.toUri()
            val remotePath = "/${userId}/${key}.jpg"
            storage.uploadFile(localUri, remotePath)
                .addOnSuccessListener { downloadUri ->
                    val reportInfo = report.copy(
                        userId = userId,
                        locationKey = locationKey,
                        sortableDate = sortableDate,
                        imageRef = downloadUri.toString(),
                        createdAt = createdAt,
                        updatedAt = now
                    )
                    root.reference.child(PATH_REPORTS).child(key).setValue(reportInfo)
                }
                .addOnFailureListener { e ->
                    Log.e("ReportRepository", "Failed to upload image: ${e.message}")
                }
        } else {
            val reportInfo = report.copy(
                userId = userId,
                locationKey = locationKey,
                sortableDate = sortableDate,
                createdAt = createdAt,
                updatedAt = now
            )
            root.reference.child(PATH_REPORTS).child(key).setValue(reportInfo)
        }
    }

    fun deleteReport(key: String, imageRef: String?) {
        // Safe deletion of pictures from storage and reports from the database
        // Altered by AI.
        if (!imageRef.isNullOrEmpty() && imageRef.startsWith("http")) {
            try {
                Firebase.storage.getReferenceFromUrl(imageRef).delete()
                    .addOnFailureListener { e ->
                        Log.e("ReportRepository", "Failed to delete storage file: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.e("ReportRepository", "Invalid storage URL: $imageRef")
            }
        }

        root.reference
            .child(PATH_REPORTS)
            .child(key)
            .removeValue()
            .addOnSuccessListener {
                Log.d("ReportRepository", "Report $key deleted.")
            }
            .addOnFailureListener { e ->
                Log.e("ReportRepository", "Failed to delete database entry: ${e.message}")
            }
    }


    fun getReports(): Query = root.reference
        .child(PATH_REPORTS)
        .orderByChild(CHILD_SORTABLE_DATE)

    fun getReportsAtDate(date: String): Query = root.reference
        .child(PATH_REPORTS)
        .orderByChild("date")
        .equalTo(date)

    fun getReportsByUser(userId: String): Query = root.reference
        .child(PATH_REPORTS)
        .orderByChild("userId")
        .equalTo(userId)

    fun getReportsByLocation(longitude: Double, latitude: Double): Query = root.reference
        .child(PATH_REPORTS)
        .orderByChild(CHILD_LOCATION_KEY)
        .equalTo(generateLocationKey(longitude, latitude))


    // The following segments were created to ensure proper functioning of the FirebaseRecyclerAdapters.
    // I have 4 different types of display lists depending on the filtering condition of the reports.
    // Written by me
    fun reportOptionsList(lifecycleOwner: LifecycleOwner): FirebaseRecyclerOptions<Report> =
        FirebaseRecyclerOptions.Builder<Report>()
            .setQuery(getReports(), Report::class.java)
            .setLifecycleOwner(lifecycleOwner)
            .build()

    fun reportOptionsAtDate(date: String, lifecycleOwner: LifecycleOwner): FirebaseRecyclerOptions<Report> =
        FirebaseRecyclerOptions.Builder<Report>()
            .setQuery(getReportsAtDate(date), Report::class.java)
            .setLifecycleOwner(lifecycleOwner)
            .build()

    fun reportOptionsByUser(userId: String, lifecycleOwner: LifecycleOwner): FirebaseRecyclerOptions<Report> =
        FirebaseRecyclerOptions.Builder<Report>()
            .setQuery(getReportsByUser(userId), Report::class.java)
            .setLifecycleOwner(lifecycleOwner)
            .build()

    fun reportOptionsByLocation(longitude: Double, latitude: Double, lifecycleOwner: LifecycleOwner): FirebaseRecyclerOptions<Report> =
        FirebaseRecyclerOptions.Builder<Report>()
            .setQuery(getReportsByLocation(longitude, latitude), Report::class.java)
            .setLifecycleOwner(lifecycleOwner)
            .build()


    fun getSingleReport(key: String): Query = root.reference
        .child(PATH_REPORTS)
        .child(key)
}

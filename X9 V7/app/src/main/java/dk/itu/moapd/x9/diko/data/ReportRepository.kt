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

class ReportRepository (
    // Defines the ReportRepository class.
    // It is used as a simulation for a storage of reports.
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val root: FirebaseDatabase=FirebaseDatabase.getInstance()
) {
    companion object {

        private const val PATH_REPORTS = "reports"

        /**
         * The child key for the "userId" field in the database.
         */
        private const val PATH_PEOPLE = "users"


        /**
         * The child key for the "createdAt" field in the database.
         */
        private const val CHILD_CREATED_AT = "createdAt"
    }

    fun currentUserId(): String? = auth.currentUser?.uid

    fun addReport(userId: String, report: Report, now: Long = System.currentTimeMillis()) {
        val key = root.reference
            .child(PATH_REPORTS)
            .push()
            .key ?: return
        val reportInfo = Report(
            userId = userId,
            title = report.title,
            location = report.location,
            longitude = report.longitude,
            latitude = report.latitude,
            date = report.date,
            type = report.type,
            description = report.description,
            severity = report.severity,
            createdAt = now,
            updatedAt = now)
        root.reference
            .child(PATH_REPORTS)
            .child(key)
            .setValue(reportInfo)

        val userReportsRef = root.reference
            .child(PATH_PEOPLE)
            .child(userId)
            .child("numReports")

        userReportsRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val currentValue = currentData.getValue(Int::class.java)

                if (currentValue == null) {
                    currentData.value = 1
                } else {
                    currentData.value = currentValue + 1
                }

                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    Log.e("Firebase", "Failed to update count", error.toException())
                }
            }
        })
        }

    fun updateReport(userId: String, key: String, report: Report, createdAt: Long?, now: Long = System.currentTimeMillis()) {
        val reportInfo = Report(
            userId = userId,
            title = report.title,
            location = report.location,
            longitude = report.longitude,
            latitude = report.latitude,
            date = report.date,
            type = report.type,
            description = report.description,
            severity = report.severity,
            createdAt = createdAt,
            updatedAt = now)
        root.reference
            .child(PATH_REPORTS)
            .child(key)
            .setValue(reportInfo)
    }

    fun deleteReport(key: String)
    {
        root.reference
            .child(PATH_REPORTS)
            .child(key)
            .removeValue()
    }


    fun getReports(): Query = root.reference
        .child(PATH_REPORTS)
        .orderByChild(CHILD_CREATED_AT)


    fun getReportsAtDate(date: String): Query {
         return root.reference
                    .child(PATH_REPORTS)
                    .orderByChild("date")
                    .equalTo(date)
    }

    fun getReportsByUser(userId: String): Query {
        return root.reference
            .child(PATH_REPORTS)
            .orderByChild("userId")
            .equalTo(userId)
    }

    fun reportOptionsList(
        lifecycleOwner: LifecycleOwner
    ): FirebaseRecyclerOptions<Report> {
        return FirebaseRecyclerOptions.Builder<Report>()
            .setQuery(getReports(), Report::class.java)
            .setLifecycleOwner(lifecycleOwner)
            .build()
    }

    fun reportOptionsAtDate(
        date: String,
        lifecycleOwner: LifecycleOwner
    ): FirebaseRecyclerOptions<Report> {
        return FirebaseRecyclerOptions.Builder<Report>()
            .setQuery(getReportsAtDate(date), Report::class.java)
            .setLifecycleOwner(lifecycleOwner)
            .build()
    }

    fun reportOptionsByUser(
        userId: String,
        lifecycleOwner: LifecycleOwner
    ): FirebaseRecyclerOptions<Report> {
        return FirebaseRecyclerOptions.Builder<Report>()
            .setQuery(getReportsByUser(userId), Report::class.java)
            .setLifecycleOwner(lifecycleOwner)
            .build()
    }

    fun getReport( key: String): Query {
        return root.reference
                    .child(PATH_REPORTS)
                    .child(key)
    }
}

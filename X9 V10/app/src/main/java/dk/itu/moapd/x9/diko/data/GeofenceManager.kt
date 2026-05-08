package dk.itu.moapd.x9.diko.data


import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dk.itu.moapd.x9.diko.broadcasts.GeofenceReceiver
import dk.itu.moapd.x9.diko.model.Report

class GeofenceManager(private val context: Context) {
    /**
     * I found the initial example online then I build it into my system. No AI used.
     * I fit it into my geofence system and added some additional complexity to replicate real application.
     * The manager creates geofence objects and sets the communication between the client and the receiver.
     */

    private val client = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        // Create a PendingIntent for the BroadcastReceiver
        val intent = Intent(context, GeofenceReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun addGeofence(report: Report) {
    // Creates a geofence object, according to my restrictions and design.
        val createdAt = (report.createdAt ?: System.currentTimeMillis()).let {
            if (it < 10_000_000_000L) it * 1000 else it
        }
        val updatedAt = (report.updatedAt ?: System.currentTimeMillis()).let {
            if (it < 10_000_000_000L) it * 1000 else it
        }
        val geofenceId = "${report.type}_${report.latitude}_${report.longitude}_$createdAt"
        val severityLevel=when (report.severity){
            "Minor" -> 1
            "Medium" -> 2
            "Major" -> 3
            else -> 0
        }
        val duration = when (report.type) {
            "Incident", "Heavy Traffic", "Other", "Police" -> {
                1L * 60 * 60 * 1000 * severityLevel // 1 hours in milliseconds for Minor case
            }
            "Maintenance" -> {
                1L * 24 * 60 * 60 * 1000 * severityLevel // 1 days in milliseconds for Minor case
            }
            else -> {
                Geofence.NEVER_EXPIRE
            }
        }

        if (duration == Geofence.NEVER_EXPIRE || updatedAt + duration > System.currentTimeMillis())
        {
            val remainingDuration =
                (updatedAt + duration) - System.currentTimeMillis()

            val geofence = Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(
                    report.latitude,
                    report.longitude,
                    200f
                )
                .setExpirationDuration(remainingDuration)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .build()

            val request = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

            client.addGeofences(request, geofencePendingIntent).run {
                addOnSuccessListener {
                    Log.d("GEOFENCE", "Geofence added: $geofenceId")
                }
                    .addOnFailureListener { e ->
                        Log.e("GEOFENCE", "Failed to add geofence", e)
                    }
            }
        }
        else
        {
            return
        }
    }


}

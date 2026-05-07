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

    private val client = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceReceiver::class.java)

        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun addGeofence(report: Report) {

        val createdAt = (report.createdAt ?: System.currentTimeMillis()).let {
            if (it < 10_000_000_000L) it * 1000 else it
        }
        val geofenceId = "${report.type}_${report.latitude}_${report.longitude}_$createdAt"
        
        val duration = when (report.type) {
            "Incident", "Heavy Traffic", "Other", "Police" -> {
                1L * 60 * 60 * 1000 // 1 hours in milliseconds
            }

            "Maintenance" -> {
                1L * 24 * 60 * 60 * 1000 // 1 days in milliseconds

            }

            else -> {
                Geofence.NEVER_EXPIRE
            }
        }

        if (duration == Geofence.NEVER_EXPIRE || createdAt + duration > System.currentTimeMillis())
        {
            val remainingDuration =
                (createdAt + duration) - System.currentTimeMillis()

            val geofence = Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(
                    report.latitude,
                    report.longitude,
                    100f
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

            client.addGeofences(request, geofencePendingIntent)
                .addOnSuccessListener {
                    Log.d("GEOFENCE", "Geofence added: $geofenceId")
                }
                .addOnFailureListener { e ->
                    Log.e("GEOFENCE", "Failed to add geofence", e)
                }
        }
        else
        {
            return
        }
    }


}

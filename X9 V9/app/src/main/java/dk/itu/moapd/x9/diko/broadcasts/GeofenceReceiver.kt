package dk.itu.moapd.x9.diko.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent)
        Log.e("GEOFENCE", "RECEIVER TRIGGERED")

        if (event == null) {
            Log.e("GEOFENCE", "Null geofencing event")
            return
        }

        if (event.hasError()) {
            Log.e("GEOFENCE", "Error code: ${event.errorCode}")
            return
        }

        val transition = event.geofenceTransition
        val message = when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "Entered geofence"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "Exited geofence"
            else -> "Unknown geofence transition"
        }

        playNotificationSound(context)
        val triggering = event.triggeringGeofences
        triggering?.forEach {
            Log.d("GEOFENCE", "Triggered: ${it.requestId}, $message")
        }
    }

    private fun playNotificationSound(context: Context) {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

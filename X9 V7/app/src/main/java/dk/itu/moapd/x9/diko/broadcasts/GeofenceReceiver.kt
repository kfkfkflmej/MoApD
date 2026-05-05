package dk.itu.moapd.x9.diko.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent)

        if (event?.hasError() ?: true) return

        when (event.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                // User entered area

            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                // User left area
            }
        }
    }
}
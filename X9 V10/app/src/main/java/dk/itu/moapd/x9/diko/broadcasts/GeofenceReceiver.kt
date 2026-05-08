package dk.itu.moapd.x9.diko.broadcasts

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.app.X9Application
import dk.itu.moapd.x9.diko.ui.main.MainActivity

private const val TAG = "GeofenceReceiver"

class GeofenceReceiver : BroadcastReceiver() {
    /**
     * I found the initial example online then I build it into my system.
     * The sendNotification was added bt AI
     * The GeofenceReceiver is set to listen to the ACTION_GEOFENCE_EVENT,
     * which occurs when the user enters or exits a geofence.
     * The system crates and sends the notification tho the Main Activity.
     */
    companion object {
        const val ACTION_GEOFENCE_EVENT = "dk.itu.moapd.x9.diko.ACTION_GEOFENCE_EVENT"
        const val EXTRA_MESSAGE = "extra_message"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        
        if (event.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(event.errorCode)
            Log.e(TAG, "Geofence error: $errorMessage")
            return
        }

        val transition = event.geofenceTransition
        val message = when (transition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> context.getString(R.string.geofence_enter)
            Geofence.GEOFENCE_TRANSITION_EXIT -> context.getString(R.string.geofence_exit)
            else -> null
        }

        if (message != null) {
            playNotificationSound(context)
            sendNotification(context, message)
            
            // Broadcast the message to MainActivity to show a Snackbar
            val broadcastIntent = Intent(ACTION_GEOFENCE_EVENT).apply {
                putExtra(EXTRA_MESSAGE, message)
                setPackage(context.packageName)
            }
            context.sendBroadcast(broadcastIntent)

            event.triggeringGeofences?.forEach {
                Log.d(TAG, "Triggered: ${it.requestId}, $message")
            }
        }
    }

    /**
     * Creates and displays a system notification.
     */
    private fun sendNotification(context: Context, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, X9Application.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.geofence_alert))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun playNotificationSound(context: Context) {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play sound: ${e.message}")
        }
    }
}

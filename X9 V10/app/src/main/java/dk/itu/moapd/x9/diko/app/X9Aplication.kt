package dk.itu.moapd.x9.diko.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.android.material.color.DynamicColors
import com.google.firebase.database.FirebaseDatabase

class X9Application : Application()  {

    companion object {
        const val CHANNEL_ID = "geofence_notifications"
    }

    override fun onCreate() {
        super.onCreate()

        // Apply dynamic colors to activities if available.
        DynamicColors.applyToActivitiesIfAvailable(this)

        // Enable disk persistence and keep the root reference synchronized.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseDatabase.getInstance().reference.keepSynced(true)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Build by AI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "Geofence Notifications"
            val descriptionText = "Notifications for geofence transitions"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(
                CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
            }

            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}
package dk.itu.moapd.x9.diko.app

import com.google.android.material.color.DynamicColors
import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class X9Application : Application()  {
    override fun onCreate() {
        super.onCreate()

    // Apply dynamic colors to activities if available.
        DynamicColors.applyToActivitiesIfAvailable(this)

        // Enable disk persistence and keep the root reference synchronized.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseDatabase.getInstance().reference.keepSynced(true)
    }
}
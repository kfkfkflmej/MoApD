package dk.itu.moapd.x9.diko.app

import com.google.android.material.color.DynamicColors
import android.app.Application

class X9Application : Application()  {
    override fun onCreate() {
        super.onCreate()

    // Apply dynamic colors to activities if available.
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
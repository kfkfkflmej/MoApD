package dk.itu.moapd.x9.diko.permisions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object CameraPermissionHelper {

    /**
     * Returns true if the app has permission to use the camera.
     *
     * @param context The application context.
     *
     * @return True if the app has permission to use the camera, false otherwise.
     */
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * The camera permission string.
     *
     * Useful to avoid duplicating Manifest.permission.CAMERA everywhere.
     */
    const val CAMERA_PERMISSION: String = Manifest.permission.CAMERA
}

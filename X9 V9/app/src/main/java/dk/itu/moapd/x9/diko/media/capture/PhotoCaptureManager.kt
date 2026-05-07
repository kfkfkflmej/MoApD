package dk.itu.moapd.x9.diko.media.capture

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoCaptureManager {

    /**
     * Photo filename format.
     */
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

    /**
     * MIME type for JPEG images.
     */
    private const val MIME_TYPE_JPEG = "image/jpeg"

    /**
     * Takes a photo using [imageCapture] and saves it into MediaStore (DCIM).
     *
     * @param context Used to obtain the main executor.
     * @param contentResolver ContentResolver used to write to MediaStore.
     * @param imageCapture CameraX ImageCapture use case.
     * @param onSaved Called when image is saved, providing the [android.net.Uri] (may be null on some devices)
     *      and the created filename.
     * @param onError Called when capture fails.
     */
    fun takePhoto(
        context: Context,
        contentResolver: ContentResolver,
        imageCapture: ImageCapture,
        onSaved: (savedUri: Uri?, filename: String) -> Unit,
        onError: (message: String, exception: ImageCaptureException?) -> Unit,
    ) {
        val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
        val filename = "IMG_$timestamp.jpg"

        val contentValues = buildContentValues(filename)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues,
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onSaved(output.savedUri, filename)
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception.message ?: "Unknown error", exception)
                }
            },
        )
    }

    /**
     * Build content values used to store photos using [MediaStore].
     *
     * @param filename The output filename.
     */
    private fun buildContentValues(filename: String): ContentValues {
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE_JPEG)
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }
    }
}
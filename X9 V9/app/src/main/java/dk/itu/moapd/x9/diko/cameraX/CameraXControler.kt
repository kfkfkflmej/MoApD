package dk.itu.moapd.x9.diko.cameraX

import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object CameraXController {

    /**
     * Starts the camera preview and sets up an ImageCapture use case.
     *
     * @param fragment Lifecycle owner used for binding.
     * @param selector Which camera should be used (front/back).
     * @param viewFinder PreviewView that will render the camera preview.
     * @param onImageCaptureReady Callback with the created ImageCapture instance.
     * @param onCanSwitchCamera Callback informing whether front+back cameras are available.
     * @param onError Callback for error messages.
     */
    fun startCamera(
        fragment: Fragment,
        selector: CameraSelector,
        viewFinder: PreviewView,
        onImageCaptureReady: (ImageCapture) -> Unit,
        onCanSwitchCamera: (Boolean) -> Unit,
        onError: (String) -> Unit,
    ) {
        val context = fragment.requireContext()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener(
            {
                val cameraProvider = try {
                    cameraProviderFuture.get()
                } catch (t: Throwable) {
                    onError("Failed to get camera provider: ${t.message ?: t}")
                    return@addListener
                }

                val preview = Preview.Builder()
                    .build()
                    .also { it.surfaceProvider = viewFinder.surfaceProvider }

                val imageCapture = ImageCapture.Builder()
                    .build()

                try {
                    // Unbind before rebinding.
                    cameraProvider.unbindAll()

                    // Bind use cases to the Fragment lifecycle.
                    cameraProvider.bindToLifecycle(
                        fragment,
                        selector,
                        preview,
                        imageCapture,
                    )

                    onImageCaptureReady(imageCapture)
                    onCanSwitchCamera(canSwitchCamera(cameraProvider))

                } catch (t: Throwable) {
                    onError("Use case binding failed: ${t.message ?: t}")
                    onCanSwitchCamera(false)
                }
            },
            ContextCompat.getMainExecutor(context),
        )
    }

    /**
     * Checks if the Android device has two cameras and it is possible to change the used camera.
     *
     * @param provider The process camera provider.
     */
    private fun canSwitchCamera(provider: ProcessCameraProvider): Boolean {
        return try {
            provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) &&
                    provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        } catch (_: CameraInfoUnavailableException) {
            false
        }
    }
}

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
    // Taken from Fabricio's example
    // The controller handles the management of the camera and the user's interactions.
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

    private fun canSwitchCamera(provider: ProcessCameraProvider): Boolean {
        return try {
            provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) &&
                    provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        } catch (_: CameraInfoUnavailableException) {
            false
        }
    }
}

package dk.itu.moapd.x9.diko.ui.camera

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.cameraX.CameraXController
import dk.itu.moapd.x9.diko.databinding.FragmentCameraBinding
import dk.itu.moapd.x9.diko.media.capture.PhotoCaptureManager
import dk.itu.moapd.x9.diko.permisions.CameraPermissionHelper
import dk.itu.moapd.x9.diko.ui.common.showSnackBar
import kotlin.getValue

class CameraFragment : Fragment(R.layout.fragment_camera) {

    /**
     * Based on Fabricio's examples.
     * I added buttonImageViewer for selecting images from the Camera Roll.
     *
     * The fragment allows the user to take a photo or select an image from the device's gallery.
     */
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private val viewModel : CameraViewModel by activityViewModels()


    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA


    private var imageCapture: ImageCapture? = null

    /**
     * This object launches a new permission dialog and receives back the user's permission.
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera() else requireActivity().finish()
    }

    private lateinit var pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                // Save to ViewModel
                viewModel.setLocalImage(uri)
                showSnackBar(getString(R.string.image_uploaded))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCameraBinding.bind(view)

        viewModel.imageSource.observe(viewLifecycleOwner) { source ->
            // Update the local UI state if needed. Keep a small local cache so the click
            // listener below can access it without directly reading LiveData each time.
            imageUriLocal = (source as? ImageSource.Local)?.uri
        }

        // Request camera permissions.
        if (CameraPermissionHelper.hasCameraPermission(requireContext())) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // The current selected camera.
        viewModel.selector.observe(viewLifecycleOwner) {
            // Only update the local selector when ViewModel provides a non-null value.
            // This avoids resetting to DEFAULT_BACK_CAMERA on configuration change
            // when LiveData doesn't have a value yet.
            cameraSelector = it ?: cameraSelector
        }

        // Define the UI behavior.
        binding.apply {

            // Set up the listener for take photo button.
            buttonImageCapture.setOnClickListener {
                takePhoto()
            }
            buttonCameraSwitch.apply {

                // Disable the button until the camera is set up
                isEnabled = false

                setOnClickListener {
                    viewModel.onCameraSelectorChanged(
                        if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                            CameraSelector.DEFAULT_BACK_CAMERA
                        else
                            CameraSelector.DEFAULT_FRONT_CAMERA
                    )

                    // Re-start use cases to update selected camera.
                    startCamera()
                }
            }
            buttonImageViewer.setOnClickListener {
                pickImageLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    }
    private var imageUriLocal: Uri? = null

    /**
     * This method is used to start the video camera device stream.
     */
    private fun startCamera() {
        // Ensure we use the latest selector from the ViewModel (if available)
        // before starting/binding CameraX. This preserves the user's choice
        // across configuration changes (rotation).
        cameraSelector = viewModel.selector.value ?: cameraSelector

        CameraXController.startCamera(
            fragment = this,
            selector = cameraSelector,
            viewFinder = binding.viewFinder,
            onImageCaptureReady = { imageCapture = it },
            onCanSwitchCamera = { binding.buttonCameraSwitch.isEnabled = it },
            onError = ::showSnackBar,
        )
    }

    /**
     * This method is used to save a frame from the video camera device stream as a JPG photo.
     */
    private fun takePhoto() {
        val capture = imageCapture ?: return
        PhotoCaptureManager.takePhoto(
            context = requireContext(),
            contentResolver = requireActivity().contentResolver,
            imageCapture = capture,
            onSaved = { uri, _  ->
                // Persist the captured Uri into the ViewModel so it survives rotation.
                uri?.let {
                    viewModel.setLocalImage(it)
                    showSnackBar(getString(R.string.image_uploaded))
                }
            },
            onError = { message, _ ->
                showSnackBar(getString(R.string.photo_capture_failed, message))
            },
        )
    }
    private fun showSnackBar(message: String) {
        _binding?.root?.showSnackBar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

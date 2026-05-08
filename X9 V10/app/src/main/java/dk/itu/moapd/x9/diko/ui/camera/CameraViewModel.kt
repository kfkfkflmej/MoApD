package dk.itu.moapd.x9.diko.ui.camera


import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    /**
     *  Based on Fabricio's examples.
     *  Some AI was used for the design of the class.
     *
     *  Saves image data from the camera fragment and provides it to the other parts of the code.
     */
    private var _selector = MutableLiveData<CameraSelector>()
    val selector: LiveData<CameraSelector>
        get() = _selector

    // ✅ Replace BOTH Uri + URL with one source of truth
    private var _imageSource = MutableLiveData<ImageSource?>(ImageSource.None)
    val imageSource: LiveData<ImageSource?>
        get() = _imageSource

    fun onCameraSelectorChanged(selector: CameraSelector) {
        _selector.value = selector
    }

    /**
     * Called when loading from Firebase (edit mode)
     */
    fun setLocalImage(uri: Uri) {
        _imageSource.value = ImageSource.Local(uri)
    }
    fun setRemoteImage(url: String) {
        _imageSource.value = ImageSource.Remote(url)
    }

    fun clear() {
        _imageSource.value = null
    }
}
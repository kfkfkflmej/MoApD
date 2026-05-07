package dk.itu.moapd.x9.diko.ui.camera

import android.net.Uri

sealed class ImageSource {
    object None : ImageSource()
    data class Local(val uri: Uri) : ImageSource()
    data class Remote(val url: String) : ImageSource()
}

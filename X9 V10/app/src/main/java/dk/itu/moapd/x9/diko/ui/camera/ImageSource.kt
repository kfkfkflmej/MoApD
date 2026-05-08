package dk.itu.moapd.x9.diko.ui.camera

import android.net.Uri

sealed class ImageSource {
    // Proposed by AI
    // Represents the source of the image and handles all cases of image sources.
    object None : ImageSource()
    data class Local(val uri: Uri) : ImageSource()
    data class Remote(val url: String) : ImageSource()
}

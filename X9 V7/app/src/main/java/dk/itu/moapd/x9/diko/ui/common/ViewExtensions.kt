
package dk.itu.moapd.x9.diko.ui.common

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Shows a short [Snackbar] anchored to this [View].
 */
fun View.showSnackBar(
    message: CharSequence,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(this, message, duration).show()
}



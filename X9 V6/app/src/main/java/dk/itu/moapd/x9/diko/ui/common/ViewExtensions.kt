
package dk.itu.moapd.x9.diko.ui.common

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

/**
 * Shows a short [Snackbar] anchored to this [View].
 */
fun View.showSnackBar(
    message: CharSequence,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(this, message, duration).show()
}

/**
 * Converts dp units to px using this [Context]'s display metrics.
 */
fun Context.dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).roundToInt()

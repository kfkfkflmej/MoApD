package dk.itu.moapd.x9.diko.ui.common

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Based on Fabricio's examples.
 */
fun View.showSnackBar(
    message: CharSequence,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    action: (() -> Unit)? = null,
    onDismissed: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(this, message, duration)
    
    if (actionText != null && action != null) {
        snackbar.setAction(actionText) { action() }
    }
    
    if (onDismissed != null) {
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                // Only trigger the dismissal callback if the action (Undo) wasn't clicked
                if (event != DISMISS_EVENT_ACTION) {
                    onDismissed()
                }
            }
        })
    }
    
    snackbar.show()
}

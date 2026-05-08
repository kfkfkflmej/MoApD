package dk.itu.moapd.x9.diko.ui.list


import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.ui.common.showSnackBar
import androidx.core.graphics.drawable.toDrawable

/**
 * This class implements an interface to allow users swipe items to left and right in a
 * `RecyclerView`.
 */
open class SwipeActionHandler : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
) {
    /**
     * Based on Fabricio's examples.
     * I used AI help for implementing the visuals for the swipes.
     *
     * The swiper is used to define an easy way to delete or edit reports in a `RecyclerView`
     * and the current file defines the visuals for the swiping actions.
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean = false

    /**
     * Called when a `ViewHolder` is swiped by the user.
     */
    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int
    ) {
        viewHolder.itemView.showSnackBar(
            getString(viewHolder.itemView.context, R.string.item_deleted)
        )
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val context = itemView.context

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX > 0) { // Swiping Right (Update)
                // Draw Green Background
                val background = ContextCompat.getColor(context, R.color.update_green).toDrawable()
                background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                background.draw(c)

                // Draw Edit Icon
                ContextCompat.getDrawable(context, R.drawable.report)?.let { icon ->
                    DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.white))
                    val iconTop = itemView.top + (itemHeight - icon.intrinsicHeight) / 2
                    val iconMargin = (itemHeight - icon.intrinsicHeight) / 2
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + iconMargin + icon.intrinsicWidth
                    val iconBottom = iconTop + icon.intrinsicHeight
                    
                    if (dX > iconMargin + icon.intrinsicWidth) {
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        icon.draw(c)
                    }
                }
            } else if (dX < 0) { // Swiping Left (Delete)
                // Draw Red Background
                val background = ContextCompat.getColor(context, R.color.delete_red).toDrawable()
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(c)

                // Draw Delete Icon
                ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete)?.let { icon ->
                    DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.white))
                    val iconTop = itemView.top + (itemHeight - icon.intrinsicHeight) / 2
                    val iconMargin = (itemHeight - icon.intrinsicHeight) / 2
                    val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    val iconBottom = iconTop + icon.intrinsicHeight
                    
                    if (dX < -(iconMargin + icon.intrinsicWidth)) {
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        icon.draw(c)
                    }
                }
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
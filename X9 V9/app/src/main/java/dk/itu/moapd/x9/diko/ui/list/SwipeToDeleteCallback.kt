package dk.itu.moapd.x9.diko.ui.list

/*
 * MIT License
 *
 * Copyright (c) 2026 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
open class SwipeToDeleteCallback : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
) {
    /**
     * Called when `ItemTouchHelper` wants to move the dragged item from its old position to the new
     * position.
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

    /**
     * Called by ItemTouchHelper on RecyclerView's onDraw callback.
     */
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
package dk.itu.moapd.x9.diko.ui.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.model.Report

class CustomAdapter (
    context: Context,
    private val itemLayoutResId: Int,
    data: List<Report>,
): ArrayAdapter<Report>(context, itemLayoutResId, data) {
    /**
     * A set of private constants used in this class.
     */
    companion object {
        private val TAG = CustomAdapter::class.qualifiedName
    }

    /**
     * An internal view holder class used to represent the layout that shows a single `DummyModel`
     * instance in the `ListView`.
     */
    private class ViewHolder(
        view: View,
    ) {
        val title: TextView = view.findViewById(R.id.text_view_title)
        val location: TextView = view.findViewById(R.id.text_view_location)
        val date: TextView = view.findViewById(R.id.text_view_date)
        val type: ImageView = view.findViewById(R.id.icon_type)
        val severity: TextView = view.findViewById(R.id.text_view_severity)
    }
    /**
     * Get the `View` instance with information about a selected `DummyModel` from the list.
     *
     * @param position The position of the specified item.
     * @param convertView The current view holder.
     * @param parent The parent view which will group the view holder.
     *
     * @return A new view holder populated with the selected `DummyModel` data.
     */
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        // The old view should be reused, if possible. You should check that this view is non-null
        // and of an appropriate type before using.
        val view = convertView ?: LayoutInflater.from(context).inflate(itemLayoutResId, parent, false)
        val viewHolder = (view.tag as? ViewHolder) ?: ViewHolder(view)

        // Populate the view holder with the selected `Dummy` data.
        Log.d(TAG, "Populate an item at position: $position")
        getItem(position)?.let { dummy ->
            populateViewHolder(viewHolder, dummy)
        }

        // Set the new view holder and return the view object.
        view.tag = viewHolder
        return view
    }
    /**
     * Populates the given [viewHolder] with data from the provided [dummy].
     *
     * @param viewHolder The ViewHolder to populate.
     * @param dummy The Dummy object containing the data to populate the ViewHolder with.
     */
    private fun populateViewHolder(
        viewHolder: ViewHolder,
        report: Report,
    ) {
        with(viewHolder) {
            // Fill out the Material Design card.
            title.text = report.title
            location.text = report.location
            date.text = report.date

            when (report.type) {
                "Incident" -> type.setImageResource(R.drawable.type_incident)
                "Danger" -> type.setImageResource(R.drawable.type_road_danger)
                "Maintenance" -> type.setImageResource(R.drawable.type_maintenance)
                "Inspection" -> type.setImageResource(R.drawable.type_inspection)
                "Other" -> type.setImageResource(R.drawable.type_other)
            }
            severity.text = report.severity

        }
    }

}
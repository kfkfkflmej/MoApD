package dk.itu.moapd.x9.diko.ui.list

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.databinding.RowItemBinding
import dk.itu.moapd.x9.diko.model.Report

class CustomAdapter (
    private val items : List<Report>,
): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
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
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder =
        RowItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ViewHolder)

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        Log.d(TAG, "Bind item at position=$position")
        items[position].let(holder::bind)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: RowItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(report: Report) =
            with(binding) {
                textViewTitle.text = report.title
                textViewLocation.text = report.location
                textViewDate.text = report.date
                when (report.type) {
                    "Incident" -> iconType.setImageResource(R.drawable.type_incident)
                    "Danger" -> iconType.setImageResource(R.drawable.type_road_danger)
                    "Maintenance" -> iconType.setImageResource(R.drawable.type_maintenance)
                    "Inspection" -> iconType.setImageResource(R.drawable.type_inspection)
                    "Other" -> iconType.setImageResource(R.drawable.type_other)
                }
                textViewSeverity.text = report.severity
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

    }
}
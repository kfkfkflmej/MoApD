package dk.itu.moapd.x9.diko.ui.list

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.databinding.RowItemBinding
import dk.itu.moapd.x9.diko.model.Report

class CustomAdapter (
    private var items : List<Report>,
): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    // Handles the logic behind the RecyclerView via ViewHolder.
    // Retrieves the data from the ReportRepository and binds it to the ViewHolder.
    // Based on the Fabricio's repo example


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

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Report>) {
        items = newList
        notifyDataSetChanged()
    }

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
                    "Heavy Traffic" -> iconType.setImageResource(R.drawable.traffic_jam_24dp_1f1f1f_fill0_wght400_grad0_opsz24)
                    "Maintenance" -> iconType.setImageResource(R.drawable.type_maintenance)
                    "Police" -> iconType.setImageResource(R.drawable.type_police)
                    "Camera" -> iconType.setImageResource(R.drawable.speed_camera)
                    "Pothole" -> iconType.setImageResource(R.drawable.pothole)
                    "Other" -> iconType.setImageResource(R.drawable.type_other)
                }
                textViewSeverity.text = report.severity
            }

    }
}
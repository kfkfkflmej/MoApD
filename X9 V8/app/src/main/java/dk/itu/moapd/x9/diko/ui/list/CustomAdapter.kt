package dk.itu.moapd.x9.diko.ui.list

import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.UserRepository
import dk.itu.moapd.x9.diko.databinding.RowItemBinding
import dk.itu.moapd.x9.diko.model.Person
import dk.itu.moapd.x9.diko.model.Report

class CustomAdapter(
    options: FirebaseRecyclerOptions<Report>,
    private val emptyView: TextView
) : FirebaseRecyclerAdapter<Report, CustomAdapter.ViewHolder>(options) {

    private val userCache = mutableMapOf<String, String>()
    private val userRepository = UserRepository()
    private val expandedPositions = mutableSetOf<Int>()

    companion object {
        private val TAG = CustomAdapter::class.qualifiedName
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = RowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        model: Report
    ) {
        Log.d(TAG, "Bind item at position=$position")
        holder.bind(model, position)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        emptyView.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
    }

    inner class ViewHolder(
        private val binding: RowItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(report: Report, position: Int) = with(binding) {
            textViewTitle.text = report.title
            textViewLocation.text = report.location
            textViewDate.text = report.date
            textViewSeverity.text = report.severity

            when (report.type) {
                "Incident" -> iconType.setImageResource(R.drawable.type_incident)
                "Heavy Traffic" -> iconType.setImageResource(R.drawable.traffic_jam)
                "Maintenance" -> iconType.setImageResource(R.drawable.type_maintenance)
                "Police" -> iconType.setImageResource(R.drawable.type_police)
                "Camera" -> iconType.setImageResource(R.drawable.speed_camera)
                "Pothole" -> iconType.setImageResource(R.drawable.pothole)
                "Other" -> iconType.setImageResource(R.drawable.type_other)
            }

            // Handle Report Image with Glide
            if (!report.imageRef.isNullOrEmpty()) {
                imageViewReport.visibility = View.VISIBLE
                
                Glide.with(root.context)
                    .load(report.imageRef)
                    .centerCrop()
                    .into(imageViewReport)
                
                // Expansion logic
                val isExpanded = expandedPositions.contains(position)
                val params = imageViewReport.layoutParams
                params.height = if (isExpanded) {
                    ViewGroup.LayoutParams.WRAP_CONTENT
                } else {
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        150f,
                        root.context.resources.displayMetrics
                    ).toInt()
                }
                imageViewReport.layoutParams = params

                imageViewReport.setOnClickListener {
                    if (expandedPositions.contains(position)) {
                        expandedPositions.remove(position)
                    } else {
                        expandedPositions.add(position)
                    }
                    notifyItemChanged(position)
                }
            } else {
                imageViewReport.visibility = View.GONE
                Glide.with(root.context).clear(imageViewReport)
            }

            val userId = report.userId ?: return@with

            // Check the shared cache
            val cachedName = userCache[userId]
            if (cachedName != null) {
                textViewUsername.text = cachedName
            } else {
                userRepository.getUserInfo(userId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val person = snapshot.getValue(Person::class.java)
                        val name = person?.username ?: "Unknown"
                        userCache[userId] = name
                        textViewUsername.text = name
                    }
            }
        }
    }
}

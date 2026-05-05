package dk.itu.moapd.x9.diko.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.BottomSheetReportsBinding
import dk.itu.moapd.x9.diko.model.Report



private const val TAG = "ReportCalendarSheet"

class ReportsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CustomAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = CustomAdapter(emptyList())
        binding.reportsBottomSheet.layoutManager = LinearLayoutManager(requireContext())
        binding.reportsBottomSheet.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(binding.reportsBottomSheet) { view, insets ->
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = navBarHeight
            }
            insets
        }

        setupSheet()


        // Fetch reports for this date
        // Then update RecyclerView adapter
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")

        val date = arguments?.getString("date")
        binding.sheetTitle.text = getString(R.string.reports_from, date)
        refreshData(date)
    }

    private fun setupSheet() {
        val date = arguments?.getString("date")
        binding.sheetTitle.text = getString(R.string.reports_from, date)

        val data = ReportRepository.getReportsFromDate(date.toString())
        if (data.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.emptyView.visibility = View.GONE
            setupRecyclerView(data)
        }

    }
    private fun setupRecyclerView(data: List<Report>?) {
        adapter.updateData(data!!)
    }

    private fun refreshData(date: String?) {
        val updatedList = ReportRepository.getReportsFromDate(date.toString())

        adapter.updateData(updatedList)

    }
    override fun onStart() {
        super.onStart()

        binding.reportsBottomSheet.isNestedScrollingEnabled = true
        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)

            val layoutParams = it.layoutParams
            layoutParams.height = (resources.displayMetrics.heightPixels * 0.6).toInt()
            it.layoutParams = layoutParams

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

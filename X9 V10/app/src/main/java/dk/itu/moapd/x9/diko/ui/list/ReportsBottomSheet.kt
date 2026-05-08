package dk.itu.moapd.x9.diko.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.BottomSheetReportsBinding

private const val TAG = "ReportCalendarSheet"

class ReportsBottomSheet : BottomSheetDialogFragment(R.layout.bottom_sheet_reports) {

    /**
     * Used to display reports from a selected date from a calendar view.
     * Based on Fabricio's examples.
     * Written by me.
     */

    private val repository by lazy { ReportRepository() }
    private var _binding: BottomSheetReportsBinding? = null
    private val binding get() = _binding!!

    private var adapter: CustomAdapter? = null

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

        val date = arguments?.getString("date")
        val options = repository
            .reportOptionsAtDate(date = date.toString(), this)

        adapter = CustomAdapter(
            emptyView = binding.emptyView,
            options = options
        )

        setupRecyclerView(requireNotNull(adapter))
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")

        val date = arguments?.getString("date")
        binding.sheetTitle.text = getString(R.string.reports_from, date)
    }

    private fun setupRecyclerView(adapter: CustomAdapter) {
        binding.reportsBottomSheetRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            this.adapter = adapter

            ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
                val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = navBarHeight
                }
                insets
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Makes sure that the bottom sheet has enough space, so that a recycler view is properly displayed.
        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )

        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)

            val layoutParams = it.layoutParams
            layoutParams.height = (resources.displayMetrics.heightPixels * 0.6).toInt()
            it.layoutParams = layoutParams

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }

}

package dk.itu.moapd.x9.diko.ui.main.reportLog

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.FragmentReportListBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.list.CustomAdapter
import dk.itu.moapd.x9.diko.ui.list.ReportItemLongClickListener

private const val TAG = "ReportList"

class ReportListFragment : Fragment(R.layout.fragment_report_list), ReportItemLongClickListener {
    /**
     * Defines the Report List Fragment.
     * Displays a list of all submitted reports.
     * Based on the Fabricio's repo example.
     * I have added a long click listener to display a dialog with the report details.
     */
    private lateinit var binding: FragmentReportListBinding
    private var adapter: CustomAdapter? = null
    private val repository by lazy { ReportRepository() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")
        val options = repository
            .reportOptionsList(this)

        binding = FragmentReportListBinding.bind(view)
        adapter = CustomAdapter(
            emptyView = binding.emptyView,
            options = options,
            itemLongClickListener = this
        )

        setupRecyclerView(requireNotNull(adapter))

        binding.fabAddReport.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.report_flow_graph
        }
    }

    override fun onItemLongClick(report: Report, position: Int) {
        val message = when(report.description){
            "" -> getString(R.string.no_description)
            else -> report.description
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(report.title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
        adapter = null
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
    }

    private fun setupRecyclerView(adapter: CustomAdapter) =
        binding.reportListView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                reverseLayout = true
                stackFromEnd = true
            }
            itemAnimator = null
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            this.adapter = adapter


        }
}

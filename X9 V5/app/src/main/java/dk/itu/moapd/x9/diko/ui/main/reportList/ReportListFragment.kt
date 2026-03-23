package dk.itu.moapd.x9.diko.ui.main.reportList

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.FragmentReportListBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.list.CustomAdapter
import dk.itu.moapd.x9.diko.ui.report.REPORT_SUCCESSFUL
import dk.itu.moapd.x9.diko.ui.report.ReportActivity



private const val TAG = "ReportList"

class ReportListFragment : Fragment(R.layout.fragment_report_list) {
    // Defines the Report List Fragment.
    // Displays a list of all submitted reports.
    // Allows the user to add new reports by accessing the Report Activity.
    private lateinit var binding: FragmentReportListBinding
    private lateinit var adapter: CustomAdapter

    private val reportLauncher= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result -> // Reads the result of the ReportActivity and saves it into a runtime repository.
        if (result.resultCode == RESULT_OK) {
            val data : Bundle? = result.data?.getBundleExtra(REPORT_SUCCESSFUL)

            var reportData = Report("", "", "", "", "", "")
            val convertBundleToReport = reportData.convertBundleToReport(data!!)
            convertBundleToReport.let {
                ReportRepository.addReport(it)
            }


            Snackbar.make(
                binding.root,
                "Report submitted",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        binding = FragmentReportListBinding.bind(view)
        adapter = CustomAdapter(emptyList())
        binding.reportListView.layoutManager = LinearLayoutManager(requireContext())
        binding.reportListView.adapter = adapter
        setupRecyclerView()


        binding.fabAddReport.setOnClickListener {
            val intent = Intent(requireContext(), ReportActivity::class.java)
            reportLauncher.launch(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
        refreshData()
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
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
    }


    private fun refreshData() {
        val updatedList = ReportRepository.getReports()
        adapter.updateData(updatedList)
    }


    private fun setupRecyclerView() =
        // Sets up the RecyclerView for displaying the list of reports.
        // Based on the Fabricio's repo example
        with(binding.reportListView) {
            val data = ReportRepository.getReports()
            adapter = CustomAdapter(data)

            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
                val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = navBarHeight
                }
                insets
            }
        }
}
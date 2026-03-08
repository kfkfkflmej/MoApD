package dk.itu.moapd.x9.diko.ui.main.reportList

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
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

// TODO: Rename parameter arguments, choose names that match

/**
 * A simple [Fragment] subclass.
 * Use the [ReportListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportListFragment : Fragment(R.layout.fragment_report_list) {

    private lateinit var binding: FragmentReportListBinding
    private lateinit var adapter: CustomAdapter

    private val reportLauncher= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result ->
        if (result.resultCode == RESULT_OK) {
            val data : Bundle? = result.data?.getBundleExtra(REPORT_SUCCESSFUL)

            var report_data : Report = Report("", "", "", "", "", "")
            val convertBundleToReport = report_data.convertBundleToReport(data!!)
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
        binding = FragmentReportListBinding.bind(view)
        adapter = CustomAdapter(emptyList())
        binding.reportListView.layoutManager = LinearLayoutManager(requireContext())
        binding.reportListView.adapter = adapter
        setupRecyclerView()

        binding.fabAddReport?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { view, insets ->
                val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom

                /*view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = 16.dpToPx() + bottomInset
                }

                */insets
            }
        }

        binding.fabAddReport?.setOnClickListener {
            val intent = Intent(requireContext(), ReportActivity::class.java)
            reportLauncher.launch(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        val updatedList = ReportRepository.getReports()
        adapter.updateData(updatedList)
    }


    private fun setupRecyclerView() =
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
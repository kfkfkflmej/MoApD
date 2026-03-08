package dk.itu.moapd.x9.diko.ui.main.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import dk.itu.moapd.x9.diko.R
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.FragmentHomeBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.report.REPORT_SUCCESSFUL
import dk.itu.moapd.x9.diko.ui.report.ReportActivity

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

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
        binding = FragmentHomeBinding.bind(view)

        binding.actionLatestReports.setOnClickListener {
            findNavController().navigate(R.id.fragment_report_list)
        }

        binding.actionExploreMap.setOnClickListener {
            findNavController().navigate(R.id.fragment_map)
        }

        binding.actionReportProblem.setOnClickListener {
            val intent = Intent(requireContext(), ReportActivity::class.java)
            reportLauncher.launch(intent)
        }

        binding.fabAddReport.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { view, insets ->
                val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom

                /*view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        bottomMargin = 16.dpToPx() + bottomInset
                    }

                    */insets
            }
        }

        binding.fabAddReport.setOnClickListener {
            val intent = Intent(requireContext(), ReportActivity::class.java)
            reportLauncher.launch(intent)
        }

    }
}
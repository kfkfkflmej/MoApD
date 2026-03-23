package dk.itu.moapd.x9.diko.ui.main.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import dk.itu.moapd.x9.diko.R
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.FragmentHomeBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.report.REPORT_SUCCESSFUL
import dk.itu.moapd.x9.diko.ui.report.ReportActivity

private const val TAG = "HomeFragment"
class HomeFragment : Fragment(R.layout.fragment_home) {
    //Defines the Home Fragment.
    //Default fragment for the Main Activity. Used as a simple introduction to the app.
    private lateinit var binding: FragmentHomeBinding

    private val reportLauncher= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    { result -> // Reads the result of the ReportActivity and saves it into a runtime repository.
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
        // Makes shortcuts from the Home fragment to several other features.
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        binding = FragmentHomeBinding.bind(view)

        binding.actionExploreMap.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.fragment_map
        }

        binding.actionLatestReports.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.fragment_report_list
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
            // Launches the ReportActivity with implicit Intent.
            val intent = Intent(requireContext(), ReportActivity::class.java)
            reportLauncher.launch(intent)
        }

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
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
    }

}
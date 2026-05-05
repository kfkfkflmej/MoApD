package dk.itu.moapd.x9.diko.ui.main.reportLog

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.FragmentMyListBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.list.CustomAdapter



private const val TAG = "ReportList"

class MyListFragment : Fragment(R.layout.fragment_my_list) {
    // Defines the Report List Fragment.
    // Displays a list of all submitted reports.
    // Allows the user to add new reports by accessing the Report Activity.
    private lateinit var binding: FragmentMyListBinding
    private lateinit var adapter: CustomAdapter



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        binding = FragmentMyListBinding.bind(view)
        adapter = CustomAdapter(emptyList())
        binding.reportListView.layoutManager = LinearLayoutManager(requireContext())
        binding.reportListView.adapter = adapter
        setupFragmentView()


        binding.fabAddReport.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.fragment_report
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

    private fun setupFragmentView() {

        val data = ReportRepository.getReports()
        if (data.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.emptyView.visibility = View.GONE
            setupRecyclerView(data)
        }
    }
    private fun setupRecyclerView(data: List<Report>?) =
    // Sets up the RecyclerView for displaying the list of reports.
        // Based on the Fabricio's repo example
        with(binding.reportListView) {
            adapter = CustomAdapter(data!!)

            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
                val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = navBarHeight
                }
                insets
            }
        }
}
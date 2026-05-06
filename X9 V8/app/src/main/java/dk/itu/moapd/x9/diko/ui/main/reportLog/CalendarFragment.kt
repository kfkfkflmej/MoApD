package dk.itu.moapd.x9.diko.ui.main.reportLog

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.databinding.FragmentReportCalendarBinding
import dk.itu.moapd.x9.diko.ui.list.ReportsBottomSheet
import java.time.LocalDate
import java.time.format.DateTimeFormatter


private const val TAG = "CalendarReportList"

class CalendarFragment : Fragment(R.layout.fragment_report_calendar) {
    // Defines the Report List Fragment.
    // Displays a list of all submitted reports.
    // Allows the user to add new reports by accessing the Report Activity.
    private lateinit var binding: FragmentReportCalendarBinding


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        binding = FragmentReportCalendarBinding.bind(view)
        binding.calendarView.maxDate = System.currentTimeMillis()
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            showReportsForDate(selectedDate)
        }

        binding.fabAddReport.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.report_flow_graph
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showReportsForDate(date: LocalDate) {
        val bottomSheet = ReportsBottomSheet()
        // Use DateTimeFormatter for LocalDate
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = date.format(formatter)

        val bundle = Bundle().apply {
            putString("date", formattedDate)
        }

        bottomSheet.arguments = bundle
        bottomSheet.show(parentFragmentManager, "ReportsBottomSheet")
    }
}

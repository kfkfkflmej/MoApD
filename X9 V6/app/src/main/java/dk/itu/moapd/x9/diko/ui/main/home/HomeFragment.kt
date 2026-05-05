package dk.itu.moapd.x9.diko.ui.main.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import dk.itu.moapd.x9.diko.R
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dk.itu.moapd.x9.diko.databinding.FragmentHomeBinding

private const val TAG = "HomeFragment"
class HomeFragment : Fragment(R.layout.fragment_home) {
    //Defines the Home Fragment.
    //Default fragment for the Main Activity. Used as a simple introduction to the app.
    private lateinit var binding: FragmentHomeBinding



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
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.fragment_report
        }


        binding.fabAddReport.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                .selectedItemId = R.id.fragment_report
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
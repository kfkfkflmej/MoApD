package dk.itu.moapd.x9.diko.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.View
import dk.itu.moapd.x9.diko.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import dk.itu.moapd.x9.diko.data.UserRepository
import dk.itu.moapd.x9.diko.databinding.FragmentHomeBinding
import dk.itu.moapd.x9.diko.ui.common.showSnackBar
import dk.itu.moapd.x9.diko.ui.main.UserViewModel

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(R.layout.fragment_home) {
    // Defines the Home Fragment.
    // Default fragment for the Main Activity. Used as a simple introduction to the app.
    private lateinit var binding: FragmentHomeBinding
    private val userRepository by lazy { UserRepository() }

    // Use a Factory to provide the UserRepository to the ViewModel
    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(userRepository) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Makes shortcuts from the Home fragment to several other features.
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        binding = FragmentHomeBinding.bind(view)

        val userId = userRepository.currentUserId() ?: return
        userViewModel.loadUser(userId)

        // FIX: Observe the username to update the UI when it's loaded asynchronously.
        userViewModel.username.observe(viewLifecycleOwner) { username ->
            binding.textGreeting.text = getString(R.string.greeting, username ?: "Guest")
        }

        with(binding) {
            actionExploreMap.setOnClickListener { navigateTo(R.id.fragment_map) }
            actionLatestReports.setOnClickListener { navigateTo(R.id.fragment_report_list) }
            actionReportProblem.setOnClickListener { navigateTo(R.id.fragment_report) }
            fabAddReport.setOnClickListener { navigateTo(R.id.fragment_report) }

            switchNotifyDanger.setOnCheckedChangeListener { _, isChecked ->
                Log.d(TAG, "switchNotifyDanger isChecked = $isChecked")
                val message = if (isChecked) R.string.enable_fencing else R.string.disable_fencing
                view.showSnackBar(getString(message))
                userViewModel.setFencingEnabled(isChecked)
            }
        }
    }

    /**
     * Safely updates navigation selection for both Portrait and Landscape.
     */
    private fun navigateTo(destinationId: Int) {
        val activity = requireActivity()
        // Try to find bottom nav (Portrait)
        activity.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = destinationId
        // Try to find nav rail (Landscape)
        activity.findViewById<NavigationRailView>(R.id.navigation_rail)?.selectedItemId = destinationId
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

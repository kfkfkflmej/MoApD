package dk.itu.moapd.x9.diko.ui.main.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import dk.itu.moapd.x9.diko.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailView
import dk.itu.moapd.x9.diko.data.GeofenceManager
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.data.UserRepository
import dk.itu.moapd.x9.diko.databinding.FragmentHomeBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.common.showSnackBar
import dk.itu.moapd.x9.diko.ui.main.UserViewModel

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(R.layout.fragment_home) {
    // Defines the Home Fragment.
    // Default fragment for the Main Activity. Used as a simple introduction to the app.
    private lateinit var binding: FragmentHomeBinding
    private val userRepository by lazy { UserRepository() }
    private val repository by lazy { ReportRepository() }


    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_GEOFENCING = "geofencing_enabled_"
    }

    // Use a Factory to provide the UserRepository to the ViewModel
    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(userRepository) as T
            }
        }
    }

    /**
     * Launcher for requesting background location permission.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestBackgroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            setupGeofences()
        } else {
            binding.switchNotifyDanger.isChecked = false
            view?.showSnackBar(getString(R.string.permission_denied_message))
        }
    }

    /**
     * Launcher for requesting foreground location permissions.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestForegroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            // Foreground granted, now request background if needed
            checkAndRequestPermissions()
        } else {
            binding.switchNotifyDanger.isChecked = false
            view?.showSnackBar(getString(R.string.permission_denied_message))
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Makes shortcuts from the Home fragment to several other features.
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        binding = FragmentHomeBinding.bind(view)

        val userId = userRepository.currentUserId() ?: return
        userViewModel.loadUser(userId)

        // Load the locally stored geofencing state for the current user.
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isGeofencingEnabled = sharedPreferences.getBoolean(KEY_GEOFENCING + userId, false)
        userViewModel.setFencingEnabled(isGeofencingEnabled)

        // Initial setup if enabled and permission granted, but only once per session.
        if (isGeofencingEnabled && checkPermission() && !userViewModel.geofencesInitialized) {
            setupGeofences()
        }

        // Observe the username to update the UI when it's loaded asynchronously.
        userViewModel.username.observe(viewLifecycleOwner) { username ->
            binding.textGreeting.text = getString(R.string.greeting, username ?: "Guest")
            if (username != null)
                binding.textGuests.visibility = View.GONE
        }

        // Observe the geofencing state to update the switch without triggering the snackbar.
        userViewModel.geofencingEnabled.observe(viewLifecycleOwner) { isEnabled ->
            if (binding.switchNotifyDanger.isChecked != isEnabled) {
                binding.switchNotifyDanger.isChecked = isEnabled
            }
        }

        with(binding) {
            actionExploreMap.setOnClickListener { navigateTo(R.id.fragment_map) }
            actionLatestReports.setOnClickListener { navigateTo(R.id.fragment_report_list) }
            actionReportProblem.setOnClickListener { navigateTo(R.id.report_flow_graph) }
            fabAddReport.setOnClickListener { navigateTo(R.id.report_flow_graph) }

            switchNotifyDanger.setOnCheckedChangeListener { buttonView, isChecked ->
                // Only show snackbar and update ViewModel if the change was triggered by a user interaction.
                if (buttonView.isPressed) {
                    Log.d(TAG, "switchNotifyDanger isChecked = $isChecked")
                    val message = if (isChecked) R.string.enable_fencing else R.string.disable_fencing
                    view.showSnackBar(getString(message))
                    
                    // Persist the preference locally and update the ViewModel.
                    userViewModel.setFencingEnabled(isChecked)
                    sharedPreferences.edit {
                        putBoolean(KEY_GEOFENCING + userId, isChecked)
                    }

                    if (isChecked) {
                        if (checkPermission()) {
                            setupGeofences()
                        } else {
                            checkAndRequestPermissions()
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkAndRequestPermissions() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted) {
            requestForegroundPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
            return
        }

        // Foreground is granted, check background
        val backgroundGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!backgroundGranted) {
            requestBackgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            setupGeofences()
        }
    }
    /**
     * Checks if the fine location permission is granted.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermission(): Boolean {

        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val backgroundGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        Log.d("GEOFENCE", "Fine location: $fineLocationGranted")
        Log.d("GEOFENCE", "Background location: $backgroundGranted")

        return fineLocationGranted && backgroundGranted
    }
    /**
     * Fetches reports and adds geofences for each if permissions are granted.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupGeofences() {
        if (!checkPermission()) return

        repository.getReports().get().addOnSuccessListener { snapshot ->
            val reports = snapshot.children.mapNotNull { it.getValue(Report::class.java) }
            val geofenceManager = GeofenceManager(requireContext())
            for (report in reports) {
                // Re-check permission inside the loop to satisfy lint requirements.
                if (checkPermission()) {
                    geofenceManager.addGeofence(report)
                }
            }
            // Mark geofences as initialized for this session.
            userViewModel.geofencesInitialized = true
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to fetch reports for geofencing: ${e.message}")
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

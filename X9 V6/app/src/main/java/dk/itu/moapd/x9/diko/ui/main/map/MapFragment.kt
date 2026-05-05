package dk.itu.moapd.x9.diko.ui.main.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.databinding.FragmentMapBinding


private const val TAG = "Map"

class MapFragment : Fragment(R.layout.fragment_map) {
    //Defines the Map Fragment.

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    /**
     * The Google Maps object.
     */
    private var googleMap: GoogleMap? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            enableMyLocation()
        } else {
            // Use view (nullable) to avoid crashes if view is destroyed
            view?.let {
                Snackbar.make(
                    it,
                    R.string.permission_denied_message,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
    private val callback = OnMapReadyCallback { googleMap ->

        // Update the Google Maps object.
        this.googleMap = googleMap

        // We use the view's root to find out how big the system bars are.
        view?.let { fragmentView ->
            ViewCompat.setOnApplyWindowInsetsListener(fragmentView) { _, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                // It automatically pushes UI buttons below the status bar and above the navigation
                // bar.

                googleMap.setPadding(0, systemBars.top, 0, systemBars.bottom)

                insets
            }
            ViewCompat.requestApplyInsets(fragmentView)
        }

        // Add a marker in IT University of Copenhagen and move the camera.
        val itu = LatLng(55.6596, 12.5910)
        googleMap.addMarker(
            MarkerOptions().position(itu).title(getString(R.string.itu_title))
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(itu))

        // Set the Google Maps style.
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.maps_style_json)
        )

        // Enable the location layer. Request the permission if it is not granted.
        if (checkPermission()) {
            @Suppress("MissingPermission")
            googleMap.isMyLocationEnabled = true
        } else {
            requestUserPermissions()
        }
    }

    /**
     * Called immediately after `onCreateView(LayoutInflater, ViewGroup, Bundle)` has returned, but
     * before any saved state has been restored in to the view. This gives subclasses a chance to
     * initialize themselves once they know their view hierarchy has been completely created. The
     * fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by `onCreateView(LayoutInflater, ViewGroup, Bundle)`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *      saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")
        _binding = FragmentMapBinding.bind(view)

        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * This method checks if the user allows the application uses all location-aware resources to
     * monitor the user's location.
     *
     * @return A boolean value with the user permission agreement.
     */
    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Create a set of dialogs to show to the users and ask them for permissions to get the device's
     * resources.
     */
    private fun requestUserPermissions() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

    private fun enableMyLocation() {
        try {
            if (checkPermission()) {
                googleMap?.isMyLocationEnabled = true
            }
        } catch (e: SecurityException) {
            Log.d(TAG, "Cannot enable location: ${e.message}")
        }
    }
}

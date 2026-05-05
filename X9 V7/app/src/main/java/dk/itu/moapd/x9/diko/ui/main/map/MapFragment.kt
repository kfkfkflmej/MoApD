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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.FragmentMapBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.services.GeoapifyService
import dk.itu.moapd.x9.diko.ui.list.MapBottomSheet
import dk.itu.moapd.x9.diko.ui.main.report.LATITUDE_KEY
import dk.itu.moapd.x9.diko.ui.main.report.LOCATION_KEY
import dk.itu.moapd.x9.diko.ui.main.report.LONGITUDE_KEY
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


private const val TAG = "Map"

class MapFragment : Fragment(R.layout.fragment_map) {
    //Defines the Map Fragment.

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var getAddressJob: Job? = null
    /**
     * The Google Maps object.
     */
    private var googleMap: GoogleMap? = null

    /**
     * The Report repository.
     */
    private val repository = ReportRepository()

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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itu, 13f))

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

        // Fetch and display markers from Firebase.
        addMarkers()

        // Set the marker click listener.
        googleMap.setOnMarkerClickListener { marker ->
            val report = marker.tag as? Report
            if (report != null) {
                val bottomSheet = MapBottomSheet().apply {
                    arguments = Bundle().apply {
                        putString("date", report.date)
                    }
                }
                bottomSheet.show(childFragmentManager, "MapBottomSheet")
            }
            false
        }
        googleMap.setOnMapLongClickListener { latLng ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_report)
                .setMessage(getString(R.string.add_from_map))
                .setPositiveButton(R.string.button_save) { _, _ ->
                    var address: Triple<String, Double, Double>? = Triple("", 0.0, 0.0)
                    getAddressJob?.cancel()
                    getAddressJob = lifecycleScope.launch {
                         address = getAddress(latLng.latitude, latLng.longitude)
                    }
                    val bundle = Bundle().apply {
                        putString(LOCATION_KEY, address?.first)
                        putDouble(LATITUDE_KEY, address?.second ?: 0.0)
                        putDouble(LONGITUDE_KEY, address?.third ?: 0.0)
                    }
                    findNavController().navigate(R.id.action_map_to_report, bundle)
                }
                .setNegativeButton(R.string.button_cancel, null)
                .show()
        }
    }

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
        getAddressJob?.cancel()
        _binding = null
    }

    /**
     * Fetches reports from Firebase and adds them as markers on the map.
     */
    private fun addMarkers() {
        repository.getReports().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                googleMap?.let { map ->
                    map.clear()

                    // Re-add the ITU marker.
                    val itu = LatLng(55.6596, 12.5910)
                    map.addMarker(
                        MarkerOptions().position(itu).title(getString(R.string.itu_title))
                    )

                    for (reportSnapshot in snapshot.children) {
                        val report = reportSnapshot.getValue(Report::class.java)
                        report?.let {
                            val pos = LatLng(it.latitude, it.longitude)
                            val marker = map.addMarker(
                                MarkerOptions()
                                    .position(pos)
                                    .title(it.title)
                                    .snippet("${it.type}: ${it.severity}")
                            )
                            marker?.tag = it
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase error: ${error.message}")
            }
        })
    }

    private suspend fun getAddress(lat: Double, lon: Double): Triple<String, Double, Double>? {
        return GeoapifyService.reverseGeocode(lat, lon)
    }

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

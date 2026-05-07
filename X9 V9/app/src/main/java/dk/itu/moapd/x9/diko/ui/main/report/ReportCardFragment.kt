package dk.itu.moapd.x9.diko.ui.main.report

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.GeofenceManager
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.data.UserRepository
import dk.itu.moapd.x9.diko.databinding.FragmentReportBinding
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.services.GeoapifyService
import dk.itu.moapd.x9.diko.ui.camera.CameraViewModel
import dk.itu.moapd.x9.diko.ui.camera.ImageSource
import dk.itu.moapd.x9.diko.ui.common.showSnackBar
import dk.itu.moapd.x9.diko.ui.main.UserViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri
import androidx.navigation.navGraphViewModels


private const val TAG = "ReportCard"

class ReportCardFragment : Fragment(R.layout.fragment_report) {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private var searchJob: Job? = null

    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val simpleReportViewModel: ReportCardViewModel by navGraphViewModels(R.id.report_flow_graph)

    private val repository by lazy { ReportRepository() }

    private val liveDataReportViewModel: ReportUpdateViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReportUpdateViewModel(repository) as T
            }
        }
    }
    private val userRepository by lazy { UserRepository() }

    private val userViewModel: UserViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(userRepository) as T
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        _binding = FragmentReportBinding.bind(view)

        val userId = userRepository.currentUserId() ?: return
        userViewModel.loadUser(userId)

        setupUI()
        if (liveDataReportViewModel.reportKey.value == null) {
            restoreUiState()
        }
        else {
            setObservers()
        }

        // Only use the observer for things that MUST change when user data is ready
        userViewModel.username.observe(viewLifecycleOwner) { 
            // Update UI components that depend on the username if any
        }

        // Observe cameraViewModel.imageSource to update button label and preview
        cameraViewModel.imageSource.observe(viewLifecycleOwner) { source ->
            binding.buttonUploadPhoto.text = if (source is ImageSource.Local || source is ImageSource.Remote) {
                getString(R.string.show_picture)
            } else {
                getString(R.string.upload_picture)
            }
            
            // Update preview image
            when (source) {
                is ImageSource.Local -> {
                    binding.imagePreview.setImageURI(source.uri)
                }
                is ImageSource.Remote -> {
                    // For now, parse as URI. Use a library like Coil/Glide if it's a web URL.
                    binding.imagePreview.setImageURI(source.url.toUri())
                }
                else -> {
                    binding.imagePreview.setImageDrawable(null)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupUI() = with(binding) {
        // Dedicated adapter for Report Type
        val typeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.report_options,
            android.R.layout.simple_dropdown_item_1line,
        )
        autoCompleteReportType.setAdapter(typeAdapter)

        // ReportName
        textFieldReportTitle.editText?.doOnTextChanged { inputText, _, _, _ ->
            simpleReportViewModel.title = inputText.toString()
        }

        // ReportLocation - Autocomplete with Geoapify
        autoCompleteReportLocation.doOnTextChanged { inputText, _, _, _ ->
            val query = inputText.toString()
            simpleReportViewModel.location = query

            searchJob?.cancel()
            if (query.length < 3) return@doOnTextChanged

            searchJob = lifecycleScope.launch {
                delay(500) // debounce
                performLocationSearch(query)
            }
        }

        // ReportDate
        textFieldReportDate.setOnClickListener {
            val constraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build()

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .setCalendarConstraints(constraints)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.show(parentFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = sdf.format(Date(selection))
                simpleReportViewModel.date = date
                textFieldReportDate.setText(date)
            }
        }

        autoCompleteReportType.doOnTextChanged { inputText, _, _, _ ->
            simpleReportViewModel.type = inputText.toString()
        }

        // ReportDescription
        descriptionLayout.editText?.doOnTextChanged { inputText, _, _, _ ->
            simpleReportViewModel.description = inputText.toString()
        }

        // ReportSeverity
        severityGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val inputSeverity = when (checkedId) {
                    R.id.button_minor -> "Minor"
                    R.id.button_moderate -> "Moderate"
                    R.id.button_major -> "Major"
                    else -> ""
                }
                simpleReportViewModel.severity = inputSeverity
            }
        }

        buttonUploadPhoto.setOnClickListener {
            findNavController().navigate(R.id.action_report_to_camera)
        }

        buttonUploadPhoto.setOnLongClickListener {
            val source = cameraViewModel.imageSource.value
            if (source is ImageSource.Local || source is ImageSource.Remote) {
                imagePreviewCard.visibility = View.VISIBLE
                true
            } else {
                showSnackBar(getString(R.string.no_photo))
                false
            }
        }

        buttonUploadPhoto.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                imagePreviewCard.visibility = View.GONE
            }
            false
        }

        // SubmitButton
        buttonSubmit.setOnClickListener {
            if (validateFormInput()) {
                val imageUriString = when (val source = cameraViewModel.imageSource.value) {
                    is ImageSource.Local -> source.uri.toString()
                    is ImageSource.Remote -> source.url
                    else -> null
                }

                val report = Report(
                    userId = userRepository.currentUserId(),
                    title = simpleReportViewModel.title,
                    location = simpleReportViewModel.location,
                    longitude = simpleReportViewModel.longitude,
                    latitude = simpleReportViewModel.latitude,
                    date = simpleReportViewModel.date,
                    type = simpleReportViewModel.type,
                    description = simpleReportViewModel.description,
                    severity = simpleReportViewModel.severity,
                    imageRef = imageUriString
                )

                setReportData(report)
                
                if (userViewModel.geofencingEnabled.value == true) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val geofenceManager = GeofenceManager(requireContext())
                        geofenceManager.addGeofence(report)
                    } else {
                        Log.w(TAG, "ACCESS_FINE_LOCATION permission not granted. Geofence not added.")
                    }
                }

                simpleReportViewModel.clear()
                clearUI()

                showSnackBar(getString(R.string.report_submitted))

                if (parentFragmentManager.backStackEntryCount > 0) {
                    findNavController().navigateUp()
                } else {
                    findNavController().navigateUp()
                }
            } else {
                showSnackBar(getString(R.string.please_fill_all_the_fields))
            }
        }
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.fragment_report && destination.id != R.id.fragment_camera) {
                liveDataReportViewModel.clearLiveModel()
                cameraViewModel.clear()
            }
        }
    }

    private suspend fun performLocationSearch(query: String) {
        val results = GeoapifyService.autocomplete(query)
        val names = results.map { it.first }

        if (names.isNotEmpty() && isAdded) {
            val locationAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                names
            )

            binding.autoCompleteReportLocation.setAdapter(locationAdapter)
            binding.autoCompleteReportLocation.showDropDown()

            binding.autoCompleteReportLocation.setOnItemClickListener { _, _, position, _ ->
                val selected = results[position]
                simpleReportViewModel.location = selected.first
                simpleReportViewModel.latitude = selected.second
                simpleReportViewModel.longitude = selected.third

                binding.autoCompleteReportLocation.setText(selected.first, false)
            }
        }
    }

    private fun clearUI() = with(binding) {
        textFieldReportTitle.editText?.text?.clear()
        autoCompleteReportLocation.text?.clear()
        textFieldReportDate.text?.clear()
        autoCompleteReportType.text?.clear()
        descriptionLayout.editText?.text?.clear()
        severityGroup.clearChecked()
        textSeverityError.visibility = View.GONE
    }

    private fun setReportData(reportData: Report) {
        Log.d(TAG, "setReportData() called with: report_data = $reportData")
        val repository = ReportRepository()
        val currentUser = repository.currentUserId()
        if (currentUser != null) {
            val key = liveDataReportViewModel.reportKey.value
            if (key == null || key == "") {
                repository.addReport(userId = currentUser, report = reportData)
            } else {

                repository.updateReport(
                    userId = currentUser,
                    key = key,
                    report = reportData,
                    createdAt = liveDataReportViewModel.createdAt.value
                )
            }
        }
    }

    private fun setObservers() {
        liveDataReportViewModel.title.observe(viewLifecycleOwner) {
            binding.textFieldReportTitle.editText?.setText(it)
        }

        liveDataReportViewModel.location.observe(viewLifecycleOwner) {
            binding.autoCompleteReportLocation.setText(it, false)
        }

        liveDataReportViewModel.date.observe(viewLifecycleOwner) {
            binding.textFieldReportDate.setText(it)
        }

        liveDataReportViewModel.type.observe(viewLifecycleOwner) {
            binding.autoCompleteReportType.setText(it, false)
        }

        liveDataReportViewModel.description.observe(viewLifecycleOwner) {
            binding.descriptionLayout.editText?.setText(it)
        }

        liveDataReportViewModel.longitude.observe(viewLifecycleOwner) {
            simpleReportViewModel.longitude = it
        }

        liveDataReportViewModel.latitude.observe(viewLifecycleOwner) {
            simpleReportViewModel.latitude = it
        }


        liveDataReportViewModel.severity.observe(viewLifecycleOwner) {
            when (it) {
                "Minor" -> binding.severityGroup.check(R.id.button_minor)
                "Moderate" -> binding.severityGroup.check(R.id.button_moderate)
                "Major" -> binding.severityGroup.check(R.id.button_major)
            }
        }
        
        liveDataReportViewModel.imageRef.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                cameraViewModel.setRemoteImage(uri)
            }
        }
    }

    private fun restoreUiState() = with(binding) {
        textFieldReportTitle.editText?.setText(simpleReportViewModel.title)
        autoCompleteReportLocation.setText(simpleReportViewModel.location, false)
        textFieldReportDate.setText(simpleReportViewModel.date)
        autoCompleteReportType.setText(simpleReportViewModel.type, false)
        descriptionLayout.editText?.setText(simpleReportViewModel.description)

        when (simpleReportViewModel.severity) {
            "Minor" -> severityGroup.check(R.id.button_minor)
            "Moderate" -> severityGroup.check(R.id.button_moderate)
            "Major" -> severityGroup.check(R.id.button_major)
        }
    }

    private fun validateFormInput(): Boolean {
        var isValid = true
        with(binding) {
            if (simpleReportViewModel.title.isEmpty()) {
                textFieldReportTitle.error = getString(R.string.title_empty)
                isValid = false
            } else if (simpleReportViewModel.title.length < 3) {
                textFieldReportTitle.error = getString(R.string.title_too_short)
                isValid = false
            } else {
                textFieldReportTitle.error = null
            }

            if (simpleReportViewModel.location.isEmpty()) {
                textFieldReportLocation.error = getString(R.string.location_empty)
                isValid = false
            } else if (simpleReportViewModel.location.length < 3) {
                textFieldReportLocation.error = getString(R.string.location_too_short)
                isValid = false
            } else {
                textFieldReportLocation.error = null
            }

            if (simpleReportViewModel.date.isEmpty()) {
                datePicker.error = getString(R.string.date_empty)
                isValid = false
            } else {
                datePicker.error = null
            }

            if (simpleReportViewModel.type.isEmpty()) {
                reportTypeDropdown.error = getString(R.string.type_empty)
                isValid = false
            } else {
                reportTypeDropdown.error = null
            }

            if (simpleReportViewModel.severity.isEmpty()) {
                textSeverityError.visibility = View.VISIBLE
                isValid = false
            } else {
                textSeverityError.visibility = View.GONE
            }
        }
        return isValid
    }

    private fun showSnackBar(message: String) {
        _binding?.root?.showSnackBar(message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
        searchJob?.cancel()

        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}

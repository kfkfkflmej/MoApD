package dk.itu.moapd.x9.diko.ui.report

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.databinding.FragmentReportBinding
import dk.itu.moapd.x9.diko.model.Report
import java.util.Date
import java.util.Locale


private const val TAG = "ReportCard"

class ReportCardFragment : Fragment(R.layout.fragment_report) {

    private lateinit var binding: FragmentReportBinding

    private val viewModel: ReportCardViewModel by viewModels()




    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        binding = FragmentReportBinding.bind(view)


        setupUI()
        restoreUiState()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupUI() =
        with(binding) {
            val dateField = datePicker.editText as EditText
            val adapter = ArrayAdapter.createFromResource(
                this@ReportCardFragment.requireContext(),
                R.array.report_options,
                R.layout.type_dropdown_menu,
            )

            //ReportName
            textFieldReportTitle.editText?.doOnTextChanged { inputText, _, _, _ ->
                viewModel.title = inputText.toString()
            }

            //ReportLocation
            textFieldReportLocation.editText?.doOnTextChanged { inputText, _, _, _ ->
                viewModel.location = inputText.toString()
            }
            //ReportDate
            textFieldReportDate.setOnClickListener {

                val constraints = CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now())
                    .build()


                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setCalendarConstraints(constraints)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

                datePicker.show(parentFragmentManager, "DATE_PICKER")

                datePicker.addOnPositiveButtonClickListener { selection ->
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = sdf.format(Date(selection))
                    viewModel.date = date
                    dateField.setText(date)
                }
            }
            //ReportType
            autoCompleteReportType.setAdapter(adapter)
            autoCompleteReportType.doOnTextChanged { inputText, _, _, _ ->
                viewModel.type = inputText.toString()
            }
            //ReportDescription
            descriptionLayout.editText?.doOnTextChanged { inputText, _, _, _ ->
                viewModel.description = inputText.toString()
            }
            //ReportSeverity
            severityGroup.addOnButtonCheckedListener { severityGroup, checkedId, isChecked ->
                var inputSeverity = ""
                if (isChecked) {
                    when (checkedId) {
                        buttonMinor.id -> {
                            Log.d(TAG, "Minor button clicked")
                            severityGroup.check(buttonMinor.id)
                            inputSeverity="Minor"
                        }

                        buttonModerate.id -> {
                            Log.d(TAG, "Moderate button clicked")
                            inputSeverity="Moderate"
                        }

                        buttonMajor.id -> {
                            Log.d(TAG, "Major button clicked")
                            inputSeverity="Major"
                        }
                    }
                    viewModel.severity = inputSeverity
                }
            }
            //SubmitButton
            buttonSubmit.setOnClickListener {

                Log.d(TAG, "Submit button clicked")
                if (validateFormInput())
                {
                    val report = Report(
                        viewModel.title,
                        viewModel.location,
                        viewModel.date,
                        viewModel.type,
                        viewModel.description,
                        viewModel.severity
                    )

                    setReportData(report)
                    Snackbar.make(
                        binding.root,
                        "Report submitted",
                        Snackbar.LENGTH_SHORT
                    ).show()


                } else {
                    Snackbar.make(
                        buttonSubmit,
                        "Please fill all the fields",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }



    private fun setReportData(report_data: Report) {
        Log.d(TAG, "setReportData() called with: report_data = $report_data")
        ReportRepository.addReport(report_data)
    }


    private fun restoreUiState() =
        with(binding) {
            textFieldReportTitle.editText?.setText(viewModel.title)
            textFieldReportLocation.editText?.setText(viewModel.location)
            (datePicker.editText as? EditText)?.setText(viewModel.date)
            autoCompleteReportType.setText(viewModel.type, false)
            descriptionLayout.editText?.setText(viewModel.description)

            when (viewModel.severity) {
                "Minor" -> severityGroup.check(buttonMinor.id)
                "Moderate" -> severityGroup.check(buttonModerate.id)
                "Major" -> severityGroup.check(buttonMajor.id)
            }
        }
    private fun validateFormInput(): Boolean {
        var isValid = true
        with(binding) {

            Log.d(TAG, "validateFormInput() called")



            if (viewModel.title.isEmpty()) {
                textFieldReportTitle.error = "Title cannot be empty"
                isValid = false
            } else if (viewModel.title.length < 3) {
                textFieldReportTitle.error = "Title must be at least 3 characters long"
                isValid = false
            } else {
                textFieldReportTitle.error = null
            }

            if (viewModel.location.isEmpty()) {
                textFieldReportLocation.error = "Location cannot be empty"
                isValid = false
            } else if (viewModel.location.length < 3) {
                textFieldReportLocation.error = "Location must be at least 3 characters long"
                isValid = false
            }else {
                textFieldReportLocation.error = null
            }

            if (viewModel.date.isEmpty()) {
                textFieldReportDate.error = "Date cannot be empty"
                isValid = false
            } else {
                textFieldReportDate.error = null
            }

            if (viewModel.type.isEmpty()) {
                autoCompleteReportType.error = "Type cannot be empty"
                isValid = false
            } else {
                autoCompleteReportType.error = null
            }

            if (viewModel.severity.isEmpty()) {
                textSeverityError.error = "Select severity"
                textSeverityError.text = "Select severity"
                binding.textSeverityError.visibility = View.VISIBLE
                isValid = false
            } else {
                binding.textSeverityError.visibility = View.GONE
                textSeverityError.error = null
            }

        }
        return isValid
    }
}
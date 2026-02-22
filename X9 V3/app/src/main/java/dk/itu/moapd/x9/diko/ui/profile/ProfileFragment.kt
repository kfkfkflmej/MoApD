package dk.itu.moapd.x9.diko.ui.profile

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
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
import dk.itu.moapd.x9.diko.data.Profile.setProfile
import dk.itu.moapd.x9.diko.databinding.FragmentHomeBinding
import dk.itu.moapd.x9.diko.databinding.FragmentProfileBinding
import dk.itu.moapd.x9.diko.model.Person
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.profile.TAG
import java.util.Date
import java.util.Locale
import kotlin.getValue


private const val TAG = "Profile"
/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentProfileBinding.bind(view)
        setupUI()
        restoreUiState()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupUI() =
        with(binding) {
            val dateField = datePicker.editText as EditText
            val adapter = ArrayAdapter.createFromResource(
                this@ProfileFragment.requireContext(),
                R.array.gender_options,
                R.layout.type_dropdown_menu,
            )

            //ReportName
            textFieldLastName.editText?.doOnTextChanged { inputText, _, _, _ ->
                viewModel.first_name = inputText.toString()
            }

            //ReportLocation
            textFieldLastName.editText?.doOnTextChanged { inputText, _, _, _ ->
                viewModel.last_name = inputText.toString()
            }
            //ReportDate
            textFieldDateOfBirth.setOnClickListener {

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
                    viewModel.date_of_birth = date
                    dateField.setText(date)
                }
            }
            //ReportType
            autoCompleteGender.setAdapter(adapter)
            autoCompleteGender.doOnTextChanged { inputText, _, _, _ ->
                viewModel.gender = inputText.toString()
            }
            textFieldEmail.editText?.doOnTextChanged { inputText, _, _, _ ->
                viewModel.email = inputText.toString()
            }

            //SubmitButton
            buttonSave.setOnClickListener {

                Log.d(TAG, "Save button clicked")
                if (validateProfileInput())
                {
                    val profile = Person(
                        viewModel.first_name,
                        viewModel.last_name,
                        viewModel.date_of_birth,
                        viewModel.gender,
                        viewModel.email
                    )

                    setProfile(profile)
                    Snackbar.make(
                        buttonSave,
                        "Profile Updated",
                        Snackbar.LENGTH_SHORT
                    ).show()


                } else {
                    Snackbar.make(
                        buttonSave,
                        "Please fill all the fields",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun validateProfileInput(): Boolean {
        var isValid = true
        with(binding) {

            Log.d(TAG, "validateProfileInput() called")


            if (viewModel.first_name.isEmpty()) {
                textFieldLastName.error = "First name cannot be empty"
                isValid = false
            } else {
                textFieldLastName.error = null
            }

            if (viewModel.last_name.isEmpty()) {
                textFieldLastName.error = "Last name cannot be empty"
                isValid = false
            } else {
                textFieldLastName.error = null
            }

            if (viewModel.date_of_birth.isEmpty()) {
                datePicker.error = "Date cannot be empty"
                isValid = false
            } else {
                datePicker.error = null
            }

            if (viewModel.gender.isEmpty()) {
                autoCompleteGender.error = "Gender cannot be empty"
                isValid = false
            } else {
                autoCompleteGender.error = null
            }

            if (viewModel.email.isEmpty()) {
                textFieldEmail.error = "Email cannot be empty"
                isValid = false
            } else {
                textFieldEmail.error = null
            }
        }
        return isValid

    }
    private fun restoreUiState() =
        with(binding) {
            textFieldFirstName.editText?.setText(viewModel.first_name)
            textFieldLastName.editText?.setText(viewModel.last_name)
            (datePicker.editText as? EditText)?.setText(viewModel.date_of_birth)
            autoCompleteGender.setText(viewModel.gender, false)
            textFieldEmail.editText?.setText(viewModel.email)
        }
}
package dk.itu.moapd.x9.diko.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val FIRST_NAME_KEY = "FIRST_NAME_KEY"
private const val LAST_NAME_KEY = "LAST_NAME_KEY"
private const val GENDER_KEY = "GENDER_KEY"
private const val DATE_OF_BIRTH_KEY = "DATE_OF_BIRTH_KEY"
private const val EMAIL_KEY = "EMAIL_KEY"


class ProfileViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var first_name: String
        get() = savedStateHandle[FIRST_NAME_KEY] ?: ""
        set(value) = savedStateHandle.set(FIRST_NAME_KEY, value)

    var last_name: String
        get() = savedStateHandle[LAST_NAME_KEY] ?: ""
        set(value) = savedStateHandle.set(LAST_NAME_KEY, value)

    var gender: String
        get() = savedStateHandle[GENDER_KEY] ?: ""
        set(value) = savedStateHandle.set(GENDER_KEY, value)

    var date_of_birth: String
        get() = savedStateHandle[DATE_OF_BIRTH_KEY] ?: ""
        set(value) = savedStateHandle.set(DATE_OF_BIRTH_KEY, value)

    var email: String
        get() = savedStateHandle[EMAIL_KEY] ?: ""
        set(value) = savedStateHandle.set(EMAIL_KEY, value)

}
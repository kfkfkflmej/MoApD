package dk.itu.moapd.x9.diko.ui.report

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.diko.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "ReportCardViewModel"

const val TITLE_KEY = "TITLE_KEY"
const val LOCATION_KEY = "LOCATION_KEY"
const val DATE_KEY = "DATE_KEY"
const val TYPE_KEY = "TYPE_KEY"
const val DESCRIPTION_KEY = "DESCRIPTION_KEY"
const val SEVERITY_KEY = "SEVERITY_KEY"

class ReportViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReportUIState(
            title = savedStateHandle[TITLE_KEY] ?: "",
            location = savedStateHandle[LOCATION_KEY] ?: "",
            date = savedStateHandle[DATE_KEY] ?: "",
            type = savedStateHandle[TYPE_KEY] ?: "",
            description = savedStateHandle[DESCRIPTION_KEY] ?: "",
            severity = savedStateHandle[SEVERITY_KEY] ?: ""
        )
    )

    val uiState: StateFlow<ReportUIState> = _uiState.asStateFlow()

    private fun updateState(newState: ReportUIState) {
        _uiState.value = newState

        savedStateHandle[TITLE_KEY] = newState.title
        savedStateHandle[LOCATION_KEY] = newState.location
        savedStateHandle[DATE_KEY] = newState.date
        savedStateHandle[TYPE_KEY] = newState.type
        savedStateHandle[DESCRIPTION_KEY] = newState.description
        savedStateHandle[SEVERITY_KEY] = newState.severity
    }

    fun updateTitle(value: String) {
        updateState(_uiState.value.copy(title = value))
    }

    fun updateLocation(value: String) {
        updateState(_uiState.value.copy(location = value))
    }

    fun updateDate(value: String) {
        updateState(_uiState.value.copy(date = value))
    }

    fun updateType(value: String) {
        updateState(_uiState.value.copy(type = value))
    }

    fun updateDescription(value: String) {
        updateState(_uiState.value.copy(description = value))
    }

    fun updateSeverity(value: String) {
        updateState(_uiState.value.copy(severity = value))
    }
}
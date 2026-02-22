package dk.itu.moapd.x9.diko.ui.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "ReportCardViewModel"

const val TITLE_KEY = "TITLE_KEY"
const val LOCATION_KEY = "LOCATION_KEY"
const val DATE_KEY = "DATE_KEY"
const val TYPE_KEY = "TYPE_KEY"
const val DESCRIPTION_KEY = "DESCRIPTION_KEY"
const val SEVERITY_KEY = "SEVERITY_KEY"



class ReportCardViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var title: String
        get() = savedStateHandle[TITLE_KEY] ?: ""
        set(value) = savedStateHandle.set(TITLE_KEY, value)

    var location: String
        get() = savedStateHandle[LOCATION_KEY] ?: ""
        set(value) = savedStateHandle.set(LOCATION_KEY, value)

    var date: String
        get() = savedStateHandle[DATE_KEY] ?: ""
        set(value) = savedStateHandle.set(DATE_KEY, value)

    var type: String
        get() = savedStateHandle[TYPE_KEY] ?: ""
        set(value) = savedStateHandle.set(TYPE_KEY, value)

    var description: String
        get() = savedStateHandle[DESCRIPTION_KEY] ?: ""
        set(value) = savedStateHandle.set(DESCRIPTION_KEY,value)

    var severity: String
        get() = savedStateHandle[SEVERITY_KEY] ?: ""
        set(value) = savedStateHandle.set(SEVERITY_KEY,value)

}
package dk.itu.moapd.x9.diko.ui.main.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.diko.model.Report


const val TITLE_KEY = "TITLE_KEY"
const val LOCATION_KEY = "LOCATION_KEY"
const val LONGITUDE_KEY = "LONGITUDE_KEY"
const val LATITUDE_KEY = "LATITUDE_KEY"
const val DATE_KEY = "DATE_KEY"
const val TYPE_KEY = "TYPE_KEY"
const val DESCRIPTION_KEY = "DESCRIPTION_KEY"
const val SEVERITY_KEY = "SEVERITY_KEY"
const val REPORT_KEY = "REPORT_KEY"
const val CREATED_AT_KEY = "CREATED_AT_KEY"



class ReportCardViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {

    var title: String
        get() = savedStateHandle[TITLE_KEY] ?: ""
        set(value) = savedStateHandle.set(TITLE_KEY, value)

    var location: String
        get() = savedStateHandle[LOCATION_KEY] ?: ""
        set(value) = savedStateHandle.set(LOCATION_KEY, value)

    var longitude: Double
        get() = savedStateHandle[LONGITUDE_KEY] ?: 0.0
        set(value) = savedStateHandle.set(LONGITUDE_KEY, value)

    var latitude: Double
        get() = savedStateHandle[LATITUDE_KEY] ?: 0.0
        set(value) = savedStateHandle.set(LATITUDE_KEY, value)


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



    fun clear() {
        savedStateHandle[REPORT_KEY] = null
        savedStateHandle[CREATED_AT_KEY] = null
        savedStateHandle[TITLE_KEY] = ""
        savedStateHandle[LOCATION_KEY] = ""
        savedStateHandle[LONGITUDE_KEY] = 0.0
        savedStateHandle[LATITUDE_KEY] = 0.0
        savedStateHandle[DATE_KEY] = ""
        savedStateHandle[TYPE_KEY] = ""
        savedStateHandle[DESCRIPTION_KEY] = ""
        savedStateHandle[SEVERITY_KEY] = ""
    }

}
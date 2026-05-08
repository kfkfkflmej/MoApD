package dk.itu.moapd.x9.diko.ui.main.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.diko.data.ReportRepository
import dk.itu.moapd.x9.diko.model.Report

class ReportUpdateViewModel(

    /**
     * Defines the ReportUpdateViewModel for receiving and loading data from other fragments and
     * keeping the UI sTate while the user is filling the Report Card.
     * Written completely by me.
     */
    private val repository: ReportRepository
): ViewModel(){
    private val _reportKey = MutableLiveData<String?>()
    var reportKey: LiveData<String?> = _reportKey
    private val _createdAt = MutableLiveData<Long>()
    val createdAt: LiveData<Long> = _createdAt
    private val _title = MutableLiveData<String>()
    var title: LiveData<String> = _title

    private val _location = MutableLiveData<String>()
    var location: LiveData<String> = _location

    private val _longitude = MutableLiveData<Double>()
    var longitude: LiveData<Double> = _longitude

    private val _latitude = MutableLiveData<Double>()
    var latitude: LiveData<Double> = _latitude

    private val _date = MutableLiveData<String>()
    var date: LiveData<String> = _date

    private val _type = MutableLiveData<String>()
    var type: LiveData<String> = _type

    private val _description = MutableLiveData<String>()
    var description: LiveData<String> = _description

    private val _severity = MutableLiveData<String>()
    var severity: LiveData<String> = _severity

    private val _imageRef = MutableLiveData<String?>()
    var imageRef: LiveData<String?> = _imageRef

    fun loadReport(key: String, onCompleted: (() -> Unit)? = null) {
        repository.getSingleReport(key)
            .get()
            .addOnSuccessListener { snapshot ->
                val report = snapshot.getValue(Report::class.java)
                _reportKey.value = key
                _createdAt.value = report?.createdAt ?: 0
                _title.value = report?.title ?: ""
                _location.value = report?.location ?: ""
                _longitude.value = report?.longitude ?: 0.0
                _latitude.value = report?.latitude ?: 0.0
                _date.value = report?.date ?: ""
                _type.value = report?.type ?: ""
                _description.value = report?.description ?: ""
                _severity.value = report?.severity ?: ""
                _imageRef.value = report?.imageRef
                onCompleted?.invoke()
            }
    }

    fun clearLiveModel(){
        _reportKey.value = null
        _createdAt.value = 0
        _title.value = ""
        _location.value = ""
        _longitude.value = 0.0
        _latitude.value = 0.0
        _date.value = ""
        _type.value = ""
        _description.value = ""
        _severity.value = ""
        _imageRef.value = null
    }
}

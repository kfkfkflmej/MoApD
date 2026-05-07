package dk.itu.moapd.x9.diko.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.diko.data.UserRepository
import dk.itu.moapd.x9.diko.model.Person

class UserViewModel(
    private val repository: UserRepository
): ViewModel(){
    private val _username = MutableLiveData<String>()
    var username: LiveData<String> = _username
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email
    private val _numReports = MutableLiveData<Int>()
    var numReports: LiveData<Int> = _numReports

    private val _geofencingEnabled = MutableLiveData<Boolean>()
    val geofencingEnabled: LiveData<Boolean> = _geofencingEnabled

    /**
     * A flag to keep track of whether the geofences have been initialized in this session.
     */
    var geofencesInitialized = false


    fun loadUser(userId: String) {
        repository.getUserInfo(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val person = snapshot.getValue(Person::class.java)
                _username.value = person?.username ?: "Guest"
                _email.value = person?.email ?: "Guest"
                _numReports.value = person?.numReports ?: 0


            }
    }

    fun setUsername(username: String) {
        _username.value = username
    }
    fun setFencingEnabled(enabled: Boolean) {
        _geofencingEnabled.value = enabled
    }
}

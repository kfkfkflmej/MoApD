package dk.itu.moapd.x9.diko.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dk.itu.moapd.x9.diko.data.UserRepository
import dk.itu.moapd.x9.diko.model.User

class UserViewModel(
    private val repository: UserRepository
): ViewModel(){
    /**
     * Defines the UserViewModel.
     * The ViewModel is responsible for managing the user data associated with the UI.
     * Written completely by me.
     */
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
                val user = snapshot.getValue(User::class.java)
                _username.value = user?.username ?: "Guest"
                _email.value = user?.email ?: "Guest"
                _numReports.value = user?.numReports ?: 0


            }
    }

    fun setUsername(username: String) {
        _username.value = username.trim()
    }
    fun setFencingEnabled(enabled: Boolean) {
        _geofencingEnabled.value = enabled
    }
}

package dk.itu.moapd.x9.diko.data

import dk.itu.moapd.x9.diko.model.Person

object Profile {
    // Defines the Profile object.
    // It is used to store the user information after they have passed the LoginActivity.
    private var profile = Person("", "", "", "", "")

    fun setProfile(profile_data: Person) {
        this.profile = profile_data
    }

    fun getProfile(): Person {
        return this.profile
    }

    fun profileEmpty(): Boolean {
        return this.profile.isEmpty()
    }
}

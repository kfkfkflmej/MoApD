package dk.itu.moapd.x9.diko.model

import android.os.Bundle

data class Person(
    val first_name: String,
    val last_name: String,
    val date_of_birth: String,
    val gender: String,
    val email: String = ""
){
    fun isNotEmpty(): Boolean {
        return first_name.isNotEmpty() &&
                last_name.isNotEmpty() &&
                date_of_birth.isNotEmpty() &&
                gender.isNotEmpty() &&
                email.isNotEmpty()
    }
    fun convertBundleToPerson(bundle: Bundle): Person {
        return Person(
            first_name = bundle.getString("first_name", ""),
            last_name = bundle.getString("last_name", ""),
            date_of_birth = bundle.getString("date_of_birth", ""),
            gender = bundle.getString("gender", ""),
            email = bundle.getString("email", ""),
            )
    }
    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("first_name", first_name)
        bundle.putString("last_name", last_name)
        bundle.putString("date_of_birth", date_of_birth)
        bundle.putString("gender", gender)
        bundle.putString("email", email)
        return bundle
    }
}

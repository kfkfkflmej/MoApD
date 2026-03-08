package dk.itu.moapd.x9.diko.model

import android.os.Bundle

data class Person(
    val first_name: String,
    val last_name: String,
    val username: String,
    val num_reports: String,
    val email: String
){
    fun isEmpty(): Boolean {
        return first_name.isEmpty() &&
                last_name.isEmpty() &&
                username.isEmpty() &&
                num_reports.isEmpty() &&
                email.isEmpty()
    }
    fun convertBundleToPerson(bundle: Bundle): Person {
        return Person(
            first_name = bundle.getString("first_name", ""),
            last_name = bundle.getString("last_name", ""),
            username = bundle.getString("username", ""),
            num_reports = bundle.getString("gender", ""),
            email = bundle.getString("email", ""),
            )
    }
    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("first_name", first_name)
        bundle.putString("last_name", last_name)
        bundle.putString("username", username)
        bundle.putString("num_reports", num_reports)
        bundle.putString("email", email)
        return bundle
    }
}

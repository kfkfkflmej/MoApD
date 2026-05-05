package dk.itu.moapd.x9.diko.model



data class Person(
    val firstName: String,
    val lastName: String,
    val username: String,
    val numReports: String,
    val email: String
){  // Defines the Person data class.
    // It is used to store the user information and pass it between activities.

    fun isEmpty(): Boolean {
        return firstName.isEmpty() &&
                lastName.isEmpty() &&
                username.isEmpty() &&
                numReports.isEmpty() &&
                email.isEmpty()
    }
    /*
    fun convertBundleToPerson(bundle: Bundle): Person {
        return Person(
            firstName = bundle.getString("first_name", ""),
            lastName = bundle.getString("last_name", ""),
            username = bundle.getString("username", ""),
            numReports = bundle.getString("gender", ""),
            email = bundle.getString("email", ""),
            )
    }
    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("first_name", firstName)
        bundle.putString("last_name", lastName)
        bundle.putString("username", username)
        bundle.putString("num_reports", numReports)
        bundle.putString("email", email)
        return bundle
    }
     */
}

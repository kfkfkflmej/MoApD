package dk.itu.moapd.x9.diko.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import dk.itu.moapd.x9.diko.model.User

class UserRepository(

    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val root: FirebaseDatabase=FirebaseDatabase.getInstance()
) {
    /**
     * Based on Fabricio's examples.
     * After that I changed it according to the design of my application.
     */
    companion object {

        private const val PATH_PEOPLE = "users"

        /**
         * The child key for the "createdAt" field in the database.
         */
    }
    fun currentUserId(): String? = auth.currentUser?.uid

    fun saveUser(userId: String, username: String, email: String) {
        // Saves a user to the database.
        val profileInfo = User(
            username = username,
            numReports = 0,
            email = email
        )
        root.reference
            .child(PATH_PEOPLE)
            .child(userId)
            .setValue(profileInfo)
    }

    fun getUserInfo(userId: String): Query {
        // Gets a user data from the database
        return root.reference
                    .child(PATH_PEOPLE)
                    .child(userId)
    }

    fun updateUserInfo(userId: String, numReports: Int, username: String, email: String) {
        // Updates the user data in the database.
        val profileInfo = User(
            username = username,
            numReports = numReports,
            email = email
        )
        root.reference
            .child(PATH_PEOPLE)
            .child(userId)
            .setValue(profileInfo)
    }

}

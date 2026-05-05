package dk.itu.moapd.x9.diko.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import dk.itu.moapd.x9.diko.model.Person

class UserRepository(

    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val root: FirebaseDatabase=FirebaseDatabase.getInstance()
) {
    companion object {

        private const val PATH_PEOPLE = "users"

        /**
         * The child key for the "createdAt" field in the database.
         */
    }
    fun currentUserId(): String? = auth.currentUser?.uid

    fun saveUser(userId: String, username: String, email: String) {
        val profileInfo = Person(
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
        return root.reference
                    .child(PATH_PEOPLE)
                    .child(userId)
    }

    fun updateUserInfo(userId: String, numReports: Int, username: String, email: String) {
        val profileInfo = Person(
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

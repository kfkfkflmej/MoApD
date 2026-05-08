package dk.itu.moapd.x9.diko.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.ui.main.MainActivity
import dk.itu.moapd.x9.diko.ui.common.showSnackBar
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.x9.diko.data.UserRepository


class LoginActivity: AppCompatActivity() {

    /**
     * Based on Fabricio's examples.
     * After that I improved it to handle authentication without the wierd error,
     * where even after a successfully login the "authentication_failed" was triggered
     * and the user had to close the app to reload the correct user state.
     *
     */
    private val userRepository by lazy { UserRepository() }
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            startMainActivity()
        } else {
            createSignInIntent()
        }
    }


    private fun createSignInIntent() {
        // Choose authentication providers.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build())

        // Create and launch sign-in intent.
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.baseline_firebase_24)
            .setTheme(R.style.Theme_FirebaseAuthentication)
            .apply {
                setTosAndPrivacyPolicyUrls(
                    "https://firebase.google.com/terms/",
                    "https://firebase.google.com/policies/analytics"
                )
            }
            .build()
        signInLauncher.launch(signInIntent)
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            createUserIfNotExists(user)
            return
        } else {

            // User canceled sign-in flow
            if (result.idpResponse == null) {
                showSnackBar(getString(R.string.authentication_canceled))
                return
            }

            showSnackBar(getString(R.string.authentication_failed))
        }
    }
    private fun createUserIfNotExists(user: FirebaseUser) {
        val userId = user.uid
        val ref = FirebaseDatabase.getInstance()
            .reference
            .child("users")
            .child(userId)

        ref.get().addOnSuccessListener { snapshot ->
        // Handles the creation of a new user in the database given their log in preference.
            if (!snapshot.exists()) {
                if (user.isAnonymous) {
                    userRepository.saveUser(userId, "Guest", "Guest")
                } else {
                    userRepository.saveUser(
                        userId,
                        user.displayName ?: "",
                        user.email ?: ""
                    )
                }
            }

            showSnackBar(getString(R.string.user_logged_in_the_app))
            startMainActivity()
        }

    }


    private fun startMainActivity() {
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }


    private fun showSnackBar(message: String) {
        window.decorView.rootView.showSnackBar(message)
    }

}
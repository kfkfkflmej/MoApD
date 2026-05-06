package dk.itu.moapd.x9.diko.ui.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.ui.auth.LoginActivity
import dk.itu.moapd.x9.diko.ui.common.showSnackBar
import dk.itu.moapd.x9.diko.ui.settings.ui.theme.X9Theme
import androidx.core.content.edit

class SettingsActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_PROFILE_PICTURE = "profile_picture_uri_"
    }

    /**
     * An activity result launcher for picking a single visual media item from the user's library.
     */
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")

            // Store the URI in SharedPreferences using the userId as part of the key.
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                sharedPreferences.edit {
                    putString(KEY_PROFILE_PICTURE + userId, uri.toString())
                }
            }

            // Persist the permission to access this URI across app restarts.
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                findViewById<View>(android.R.id.content).showSnackBar(getString(R.string.image_uploaded))
                finish()
            } catch (e: SecurityException) {
                Log.e("PhotoPicker", "Failed to take persistable URI permission", e)
            }
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    // Sets up the Settings Activity.
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        setContent {
            X9Theme {
                MainScaffold(
                    onDone = { finish() },
                    onEditPicture = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                    onSignOut = {
                        auth.signOut()
                        finishAffinity()
                        startLoginActivity()
                                },
                )
            }
        }
    }

    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            // Alternative to calling finish(): clears the back stack.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }
}

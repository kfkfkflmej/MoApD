package dk.itu.moapd.x9.diko.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.diko.ui.auth.LoginActivity
import dk.itu.moapd.x9.diko.ui.settings.ui.theme.X9Theme

class SettingsActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

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
                    onEditPicture = { /*TODO*/ },
                    onSignOut = {
                                auth.signOut()
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

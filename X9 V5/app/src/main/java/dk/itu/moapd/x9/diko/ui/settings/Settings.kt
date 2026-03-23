package dk.itu.moapd.x9.diko.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import dk.itu.moapd.x9.diko.ui.settings.ui.theme.X9Theme

class SettingsActivity : ComponentActivity() {
    // Sets up the Settings Activity.
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            X9Theme {
                MainScaffold(
                    onDone = { finish() },
                    onEditPicture = { /*TODO*/ },
                    onSignOut = { /*TODO*/ },
                )
            }
        }
    }
}

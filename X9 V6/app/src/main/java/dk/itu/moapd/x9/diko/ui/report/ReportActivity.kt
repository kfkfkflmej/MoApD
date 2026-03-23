package dk.itu.moapd.x9.diko.ui.report

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import dk.itu.moapd.x9.diko.ui.report.theme.ReportCardTheme

private const val TAG = "ReportActivity"
//const val REPORT_SUCCESSFUL = "dk.itu.moapd.x9.diko.report_data"


class ReportActivity : ComponentActivity() {
        // Sets up the Report Activity.
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()

            setContent {
                ReportCardTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ReportScreen(
                            onBack = { finish() },
                            onSubmit = { report ->
                                setReportData(this, report)
                            }
                        )
                    }
                }
            }
        }
}


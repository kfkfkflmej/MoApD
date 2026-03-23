package dk.itu.moapd.x9.diko.ui.report


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.itu.moapd.x9.diko.ui.report.theme.ReportCardTheme
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.model.Report
import dk.itu.moapd.x9.diko.ui.report.components.DatePickerField
import dk.itu.moapd.x9.diko.ui.report.components.DescriptionField
import dk.itu.moapd.x9.diko.ui.report.components.LocationField
import dk.itu.moapd.x9.diko.ui.report.components.ReportTypeDropdown
import dk.itu.moapd.x9.diko.ui.report.components.TitleField
import kotlinx.coroutines.launch


const val REPORT_SUCCESSFUL =
    "dk.itu.moapd.x9.diko.report_data"

@Composable
fun ReportScreen (
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSubmit: (Report) -> Unit

) {
    // Sets up the Report Screen and interactable elements.
    ReportContent(
        modifier = modifier,
        onBack = onBack,
        onSubmit = onSubmit
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportContent(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSubmit: (Report) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? AppCompatActivity
    val uiState by viewModel.uiState.collectAsState()

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.report_page_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },

        snackbarHost = { SnackbarHost(snackBarHostState) }

    ) { padding ->

        Column(
            modifier = modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            TitleField(viewModel)

            LocationField(viewModel)

            DatePickerField(viewModel)

            ReportTypeDropdown(viewModel)

            DescriptionField(viewModel)

            SeveritySelector(viewModel)

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {

                    if (validateFormInput(uiState)) {

                        val report = Report(
                            title = uiState.title,
                            location = uiState.location,
                            date = uiState.date,
                            type = uiState.type,
                            description = uiState.description,
                            severity = uiState.severity
                        )

                        onSubmit(report)


                    } else {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar("Please fill all the fields")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.submit))
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}


@Composable
fun SeveritySelector(viewModel: ReportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val options = listOf("Minor", "Moderate", "Major")

    Column {

        Row {

            options.forEach { option ->

                FilterChip(
                    selected = uiState.severity == option,
                    onClick = { viewModel.updateSeverity(option) },
                    label = { Text(option) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}


fun validateFormInput(uiState: ReportUIState): Boolean {

    return uiState.title.length >= 3 &&
            uiState.location.length >= 3 &&
            uiState.date.isNotEmpty() &&
            uiState.type.isNotEmpty() &&
            uiState.severity.isNotEmpty()
}

fun setReportData(activity: Activity, report: Report) {

    val bundle = report.toBundle()

    val intent = Intent().apply {
        putExtra(REPORT_SUCCESSFUL, bundle)
    }

    activity.setResult(Activity.RESULT_OK, intent)
    activity.finish()
}

@Preview(
    name = "Preview",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL,
    apiLevel = 33
)
@Composable
private fun DefaultPreview() {
    ReportCardTheme {
        ReportContent()
    }
}

package dk.itu.moapd.x9.diko.ui.report


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.itu.moapd.x9.diko.ui.report.theme.ReportCardTheme
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.model.Report
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    val snackbarHostState = remember { SnackbarHostState() }
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

        snackbarHost = { SnackbarHost(snackbarHostState) }

    ) { padding ->

        Column(
            modifier = Modifier
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
                            snackbarHostState.showSnackbar("Please fill all the fields")
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
fun TitleField(viewModel: ReportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    OutlinedTextField(
        value = uiState.title,
        onValueChange = { viewModel.updateTitle(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.text_field_report_name)) },
        leadingIcon = {
            Icon(
                painterResource(R.drawable.car),
                contentDescription = null
            )
        }
    )
}

@Composable
fun LocationField(viewModel: ReportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    OutlinedTextField(
        value = uiState.location,
        onValueChange = { viewModel.updateLocation(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.text_field_report_location)) },
        leadingIcon = {
            Icon(
                painterResource(R.drawable.location),
                contentDescription = null
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(viewModel: ReportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }

    var showDatePicker by remember { mutableStateOf(false) }


    OutlinedTextField(
        value = uiState.date,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                showDatePicker = true
            },
        interactionSource = interactionSource,
        enabled = false,
        readOnly = true,
        label = { Text(stringResource(R.string.text_field_report_date)) },
        leadingIcon = {
            Icon(
                painterResource(R.drawable.calendar),
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.run {
            outlinedTextFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,

            )
        }
    )

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { selection ->
                selection?.let {
                    val sdf = SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    )
                    val date = sdf.format(Date(it))
                    viewModel.updateDate(date)
                }
            },
            onDismiss = { showDatePicker = false }
        )
    }
}


@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportTypeDropdown(viewModel: ReportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val options = stringArrayResource(R.array.report_options)

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = uiState.type,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.menu_report_type)) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.traffic),
                    contentDescription = null
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {

            options.forEach {

                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        viewModel.updateType(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DescriptionField(viewModel: ReportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    OutlinedTextField(
        value = uiState.description,
        onValueChange = { viewModel.updateDescription(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.text_field_description)) },
        minLines = 4,
        maxLines = 6
    )
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

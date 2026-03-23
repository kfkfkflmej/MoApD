package dk.itu.moapd.x9.diko.ui.report.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.ui.report.ReportViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(viewModel: ReportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }

    var showDatePicker by remember { mutableStateOf(false) }


        OutlinedTextField(
            value = uiState.date,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .semantics { role = Role.Button }
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null )
                {
                    showDatePicker = true
                },
            label = { Text(stringResource(R.string.text_field_report_date)) },
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.calendar),
                    contentDescription = null
                )
            },
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Transparent,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
            onDismiss = { }

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

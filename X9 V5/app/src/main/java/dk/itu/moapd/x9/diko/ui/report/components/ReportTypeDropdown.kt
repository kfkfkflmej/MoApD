package dk.itu.moapd.x9.diko.ui.report.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.ui.report.ReportViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportTypeDropdown(viewModel: ReportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val options = stringArrayResource(R.array.report_options)

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { }
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

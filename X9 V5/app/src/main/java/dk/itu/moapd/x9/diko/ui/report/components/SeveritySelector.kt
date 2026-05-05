package dk.itu.moapd.x9.diko.ui.report.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.diko.ui.report.ReportViewModel


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
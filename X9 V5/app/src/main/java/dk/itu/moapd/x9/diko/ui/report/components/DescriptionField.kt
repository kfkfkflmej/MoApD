package dk.itu.moapd.x9.diko.ui.report.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.ui.report.ReportViewModel


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
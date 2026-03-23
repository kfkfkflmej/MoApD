package dk.itu.moapd.x9.diko.ui.report.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.x9.diko.R
import dk.itu.moapd.x9.diko.ui.report.ReportViewModel

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
package dk.itu.moapd.x9.diko.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dk.itu.moapd.x9.diko.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    onDone: () -> Unit,
    onEditPicture: () -> Unit,
    onSignOut: () -> Unit,
)
{ // Sets up the Settings Screen and interactable elements.
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = stringResource(R.string.settings))
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Button(
                onClick = { onEditPicture() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.edit_picture))
            }

            Button(
                    onClick = { onSignOut() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.sign_out))
                }

            Button(
                onClick = {onDone()},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.done))
            }

        }
    }
}

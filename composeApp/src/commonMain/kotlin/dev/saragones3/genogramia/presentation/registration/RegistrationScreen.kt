package dev.saragones3.genogramia.presentation.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.registration_title
import genogramia.composeapp.generated.resources.registration_screen_placeholder
import genogramia.composeapp.generated.resources.registration_button

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onBackClick: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.registration_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(Res.string.registration_screen_placeholder))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRegistrationSuccess) {
                Text(stringResource(Res.string.registration_button))
            }
        }
    }
}

@Composable
@Preview
private fun RegistrationScreenPreview() {
    MaterialTheme {
        RegistrationScreen(onBackClick = {}, onRegistrationSuccess = {})
    }
}

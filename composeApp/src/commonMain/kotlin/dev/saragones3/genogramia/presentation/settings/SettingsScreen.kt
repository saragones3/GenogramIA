package dev.saragones3.genogramia.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.settings_title
import genogramia.composeapp.generated.resources.settings_screen_placeholder
import genogramia.composeapp.generated.resources.settings_description

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(Res.string.settings_screen_placeholder))
            Text(stringResource(Res.string.settings_description))
        }
    }
}

@Composable
@Preview
private fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen()
    }
}

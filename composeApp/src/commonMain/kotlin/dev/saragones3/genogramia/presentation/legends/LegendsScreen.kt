package dev.saragones3.genogramia.presentation.legends

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.legends_title
import genogramia.composeapp.generated.resources.legends_screen_placeholder
import genogramia.composeapp.generated.resources.legends_description

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegendsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.legends_title)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(Res.string.legends_screen_placeholder))
            Text(stringResource(Res.string.legends_description))
        }
    }
}

@Composable
@Preview
private fun LegendsScreenPreview() {
    MaterialTheme {
        LegendsScreen()
    }
}

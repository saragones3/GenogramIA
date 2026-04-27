package dev.saragones3.genogramia.presentation.legends

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.legends_description
import genogramia.composeapp.generated.resources.legends_screen_placeholder
import genogramia.composeapp.generated.resources.legends_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegendsScreen(viewModel: LegendsViewModel = koinViewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.legends_title)) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(Res.string.legends_screen_placeholder))
            Text(stringResource(Res.string.legends_description))
        }
    }
}

@Composable
@Preview
fun LegendsScreenPreview() {
    MaterialTheme {
        LegendsScreen()
    }
}

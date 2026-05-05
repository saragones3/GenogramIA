package dev.saragones3.genogramia.presentation.authenticatedhome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.auth_home_start_tree
import genogramia.composeapp.generated.resources.auth_home_title
import genogramia.composeapp.generated.resources.auth_home_welcome
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatedHomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.auth_home_title)) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(Res.string.auth_home_welcome, "[Nombre]"))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* US-011 */ }) {
                Text(stringResource(Res.string.auth_home_start_tree))
            }
        }
    }
}

@Composable
@Preview
private fun AuthenticatedHomeScreenPreview() {
    MaterialTheme {
        AuthenticatedHomeScreen()
    }
}

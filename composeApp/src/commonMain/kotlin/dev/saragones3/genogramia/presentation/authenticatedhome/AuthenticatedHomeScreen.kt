package dev.saragones3.genogramia.presentation.authenticatedhome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.auth_home_title
import genogramia.composeapp.generated.resources.auth_home_welcome
import genogramia.composeapp.generated.resources.auth_home_start_tree

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatedHomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.auth_home_title)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
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

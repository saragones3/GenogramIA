package dev.saragones3.genogramia.presentation.guesthome

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.app_name
import genogramia.composeapp.generated.resources.login_title
import genogramia.composeapp.generated.resources.guest_home_welcome
import genogramia.composeapp.generated.resources.guest_home_start_tree

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestHomeScreen(
    onLoginClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_name)) },
                actions = {
                    Button(onClick = onLoginClick) {
                        Text(stringResource(Res.string.login_title))
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
            Text(stringResource(Res.string.guest_home_welcome))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* US-009: Start first tree */ }) {
                Text(stringResource(Res.string.guest_home_start_tree))
            }
        }
    }
}

@Composable
@Preview
private fun GuestHomeScreenPreview() {
    MaterialTheme {
        GuestHomeScreen(onLoginClick = {})
    }
}

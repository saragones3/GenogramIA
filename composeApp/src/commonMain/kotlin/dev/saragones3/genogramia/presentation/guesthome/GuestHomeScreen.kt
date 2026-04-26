package dev.saragones3.genogramia.presentation.guesthome

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
import genogramia.composeapp.generated.resources.app_name
import genogramia.composeapp.generated.resources.guest_home_start_tree
import genogramia.composeapp.generated.resources.guest_home_welcome
import genogramia.composeapp.generated.resources.login_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestHomeScreen(onLoginClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_name)) },
                actions = {
                    Button(onClick = onLoginClick) {
                        Text(stringResource(Res.string.login_title))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
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
fun GuestHomeScreenPreview() {
    MaterialTheme {
        GuestHomeScreen(onLoginClick = {})
    }
}

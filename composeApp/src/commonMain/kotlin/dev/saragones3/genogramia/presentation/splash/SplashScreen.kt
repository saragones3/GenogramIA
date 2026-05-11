package dev.saragones3.genogramia.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    onNavigateToGuestHome: () -> Unit,
    onNavigateToAuthenticatedHome: () -> Unit,
) {
    val viewModel: SplashViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            SplashUiState.Loading -> { /* Keep showing splash */ }

            SplashUiState.NavigateToGuestHome -> {
                onNavigateToGuestHome()
            }

            SplashUiState.NavigateToAuthenticatedHome -> {
                onNavigateToAuthenticatedHome()
            }
        }
    }
    SplashContent()
}

@Composable
private fun SplashContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
@Preview
private fun SplashScreenPreview() {
    GenogramiaTheme {
        SplashContent()
    }
}

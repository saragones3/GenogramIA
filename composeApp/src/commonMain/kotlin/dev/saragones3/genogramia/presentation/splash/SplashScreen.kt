package dev.saragones3.genogramia.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme

@Composable
fun SplashScreen(
    uiState: SplashUiState,
    onNavigateToGuestHome: () -> Unit,
    onNavigateToAuthenticatedHome: () -> Unit,
) {
    LaunchedEffect(uiState) {
        when (uiState) {
            SplashUiState.Loading -> { /* Keep showing splash */ }
            SplashUiState.NavigateToGuestHome -> onNavigateToGuestHome()
            SplashUiState.NavigateToAuthenticatedHome -> onNavigateToAuthenticatedHome()
        }
    }

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
        SplashScreen(
            uiState = SplashUiState.Loading,
            onNavigateToGuestHome = {},
            onNavigateToAuthenticatedHome = {},
        )
    }
}

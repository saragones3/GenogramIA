package dev.saragones3.genogramia.presentation.splash

sealed interface SplashUiState {
    data object Loading : SplashUiState

    data object NavigateToGuestHome : SplashUiState

    data object NavigateToAuthenticatedHome : SplashUiState
}

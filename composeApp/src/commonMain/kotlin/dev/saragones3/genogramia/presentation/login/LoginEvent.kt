package dev.saragones3.genogramia.presentation.login

sealed interface LoginEvent {
    data class OnDataChanged(
        val email: String,
        val password: String,
    ) : LoginEvent

    data object OnLoginClicked : LoginEvent

    data object OnErrorShown : LoginEvent

    data object OnLoginSuccessConsumed : LoginEvent

    data object OnBackClicked : LoginEvent

    data object OnRegisterClicked : LoginEvent

    data object OnForgotPasswordClicked : LoginEvent

    data object OnGuestClicked : LoginEvent
}

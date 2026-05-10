package dev.saragones3.genogramia.presentation.registration

sealed interface RegistrationEvent {
    data class OnDataChanged(
        val name: String,
        val email: String,
        val password: String,
    ) : RegistrationEvent

    data object OnSignUpClicked : RegistrationEvent

    data object OnGoogleSignUpClicked : RegistrationEvent

    data object OnAppleSignUpClicked : RegistrationEvent

    data object OnRegistrationSuccessConsumed : RegistrationEvent
}

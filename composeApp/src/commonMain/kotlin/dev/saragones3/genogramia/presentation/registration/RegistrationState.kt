package dev.saragones3.genogramia.presentation.registration

import org.jetbrains.compose.resources.StringResource

data class RegistrationState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val nameError: ValidationError? = null,
    val emailError: ValidationError? = null,
    val passwordError: ValidationError? = null,
    val generalError: StringResource? = null,
    val isRegistrationSuccess: Boolean = false,
) {
    enum class ValidationError {
        EMPTY,
        INVALID,
    }
}

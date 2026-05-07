package dev.saragones3.genogramia.presentation.registration

data class RegistrationState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val nameError: ValidationError? = null,
    val emailError: ValidationError? = null,
    val passwordError: ValidationError? = null,
    val generalError: String? = null,
    val isRegistrationSuccess: Boolean = false,
) {
    enum class ValidationError {
        EMPTY,
        INVALID,
    }
}

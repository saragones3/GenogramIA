package dev.saragones3.genogramia.presentation.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: ValidationError? = null,
    val passwordError: ValidationError? = null,
    val generalError: LoginError? = null,
    val isSuccess: Boolean = false,
) {
    enum class ValidationError {
        EMPTY,
        INVALID,
    }
}

enum class LoginError {
    WrongCredentials,
}

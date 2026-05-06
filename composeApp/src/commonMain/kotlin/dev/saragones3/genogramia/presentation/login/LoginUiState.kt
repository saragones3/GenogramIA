package dev.saragones3.genogramia.presentation.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: LoginError? = null,
    val isSuccess: Boolean = false,
)

enum class LoginError {
    EmptyValues,
    WrongCredentials,
}

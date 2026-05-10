package dev.saragones3.genogramia.presentation.forgotpassword

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: ValidationError? = null,
    val isSuccess: Boolean = false,
    val error: ForgotPasswordError? = null,
) {
    enum class ValidationError {
        EMPTY,
        INVALID,
    }

    enum class ForgotPasswordError {
        UserNotFound,
        Generic,
    }
}

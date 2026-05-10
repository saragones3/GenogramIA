package dev.saragones3.genogramia.presentation.settings

import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.error_empty_fields
import genogramia.composeapp.generated.resources.error_invalid_password
import genogramia.composeapp.generated.resources.error_passwords_do_not_match
import org.jetbrains.compose.resources.StringResource

data class ChangePasswordState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val currentPasswordError: ValidationError? = null,
    val passwordError: ValidationError? = null,
    val confirmError: ValidationError? = null,
    val generalError: StringResource? = null,
) {
    enum class ValidationError(
        val message: StringResource,
    ) {
        EMPTY(Res.string.error_empty_fields),
        TOO_SHORT(Res.string.error_invalid_password),
        MISMATCH(Res.string.error_passwords_do_not_match),
    }
}

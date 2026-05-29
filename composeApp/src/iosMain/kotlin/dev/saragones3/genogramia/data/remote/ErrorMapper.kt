package dev.saragones3.genogramia.data.remote

import dev.saragones3.genogramia.domain.model.AuthError

actual fun Throwable.toAuthError(): AuthError {
    val msg = message ?: ""
    return when {
        msg.contains("ALREADY IN USE", ignoreCase = true) -> AuthError.EmailAlreadyInUse
        msg.contains("INVALID_EMAIL", ignoreCase = true) -> AuthError.InvalidEmail
        msg.contains("WEAK_PASSWORD", ignoreCase = true) -> AuthError.WeakPassword
        msg.contains("USER_NOT_FOUND", ignoreCase = true) -> AuthError.UserNotFound
        msg.contains("WRONG_PASSWORD", ignoreCase = true) -> AuthError.WrongPassword
        msg.contains("RECENT_LOGIN_REQUIRED", ignoreCase = true) -> AuthError.RequiresRecentLogin
        else -> AuthError.Unknown
    }
}

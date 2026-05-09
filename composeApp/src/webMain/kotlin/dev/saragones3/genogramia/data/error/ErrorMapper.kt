package dev.saragones3.genogramia.data.error

import dev.saragones3.genogramia.domain.model.AuthError

actual fun Throwable.toAuthError(): AuthError {
    val msg = message ?: ""
    return when {
        msg.contains("auth/email-already-in-use", ignoreCase = true) ||
            msg.contains("ALREADY IN USE", ignoreCase = true) -> AuthError.EmailAlreadyInUse

        msg.contains("auth/invalid-email", ignoreCase = true) -> AuthError.InvalidEmail

        msg.contains("auth/weak-password", ignoreCase = true) -> AuthError.WeakPassword

        msg.contains("auth/user-not-found", ignoreCase = true) -> AuthError.UserNotFound

        msg.contains("auth/wrong-password", ignoreCase = true) -> AuthError.WrongPassword

        else -> AuthError.Unknown
    }
}

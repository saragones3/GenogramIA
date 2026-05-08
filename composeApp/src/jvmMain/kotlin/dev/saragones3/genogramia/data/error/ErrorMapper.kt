package dev.saragones3.genogramia.data.error

import dev.saragones3.genogramia.domain.model.AuthError

actual fun Throwable.toAuthError(): AuthError {
    val msg = message ?: ""
    return when {
        msg.contains("ALREADY IN USE", ignoreCase = true) -> AuthError.EmailAlreadyInUse
        else -> AuthError.Unknown
    }
}

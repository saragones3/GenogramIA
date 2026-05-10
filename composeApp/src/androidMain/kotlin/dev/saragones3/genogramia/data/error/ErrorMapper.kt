package dev.saragones3.genogramia.data.error

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dev.saragones3.genogramia.domain.model.AuthError

actual fun Throwable.toAuthError(): AuthError =
    when (this) {
        is FirebaseAuthUserCollisionException -> {
            AuthError.EmailAlreadyInUse
        }

        is FirebaseAuthInvalidCredentialsException -> {
            AuthError.InvalidEmail
        }

        is FirebaseAuthWeakPasswordException -> {
            AuthError.WeakPassword
        }

        is FirebaseAuthInvalidUserException -> {
            AuthError.UserNotFound
        }

        is FirebaseAuthRecentLoginRequiredException -> {
            AuthError.RequiresRecentLogin
        }

        is FirebaseAuthException -> {
            when (errorCode) {
                "ERROR_WRONG_PASSWORD" -> AuthError.WrongPassword
                "ERROR_REQUIRES_RECENT_LOGIN" -> AuthError.RequiresRecentLogin
                else -> AuthError.Unknown
            }
        }

        else -> {
            if (message?.contains("ALREADY IN USE", ignoreCase = true) == true) {
                AuthError.EmailAlreadyInUse
            } else {
                AuthError.Unknown
            }
        }
    }

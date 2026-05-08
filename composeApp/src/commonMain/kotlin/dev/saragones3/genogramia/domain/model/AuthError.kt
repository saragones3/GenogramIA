package dev.saragones3.genogramia.domain.model

sealed class AuthError : Throwable() {
    data object EmailAlreadyInUse : AuthError()

    data object InvalidEmail : AuthError()

    data object WeakPassword : AuthError()

    data object UserNotFound : AuthError()

    data object WrongPassword : AuthError()

    data object Unknown : AuthError()
}

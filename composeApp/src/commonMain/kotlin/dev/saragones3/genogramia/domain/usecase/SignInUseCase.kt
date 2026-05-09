package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.repository.AuthRepository

class SignInUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<User> =
        runCatching {
            authRepository.signInWithEmailAndPassword(email, password)
        }
}

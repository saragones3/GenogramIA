package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.repository.AuthRepository

class SignUpUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
    ): Result<User> =
        runCatching {
            authRepository.signUpWithEmailAndPassword(name, email, password)
        }
}

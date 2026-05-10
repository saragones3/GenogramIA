package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.repository.AuthRepository

class SendPasswordResetEmailUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): Result<Unit> =
        runCatching {
            authRepository.sendPasswordResetEmail(email)
        }
}

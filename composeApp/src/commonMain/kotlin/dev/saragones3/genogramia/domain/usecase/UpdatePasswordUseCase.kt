package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.repository.AuthRepository

class UpdatePasswordUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(newPassword: String) = authRepository.updatePassword(newPassword)
}

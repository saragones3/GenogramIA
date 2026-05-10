package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.repository.AuthRepository

class UpdatePasswordUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String,
    ) {
        authRepository.reauthenticate(currentPassword)
        authRepository.updatePassword(newPassword)
    }
}

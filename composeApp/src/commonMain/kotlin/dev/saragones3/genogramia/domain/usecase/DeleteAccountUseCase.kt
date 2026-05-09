package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.repository.AuthRepository

class DeleteAccountUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() = authRepository.deleteAccount()
}

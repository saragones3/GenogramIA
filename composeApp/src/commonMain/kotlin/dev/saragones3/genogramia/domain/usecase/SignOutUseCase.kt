package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.repository.AuthRepository

class SignOutUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() = authRepository.signOut()
}

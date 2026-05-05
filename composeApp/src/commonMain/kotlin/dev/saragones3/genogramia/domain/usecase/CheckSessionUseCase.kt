package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.repository.AuthRepository

class CheckSessionUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): User? = authRepository.getCurrentUser()
}

package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.repository.TreeRepository

class DeleteTreeUseCase(
    private val repository: TreeRepository,
) {
    suspend operator fun invoke(id: String) {
        repository.deleteTree(id)
    }
}

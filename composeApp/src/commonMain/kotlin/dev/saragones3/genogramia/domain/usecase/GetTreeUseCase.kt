package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.TreeRepository

class GetTreeUseCase(
    private val repository: TreeRepository,
) {
    suspend operator fun invoke(id: String): GenogramTree? = repository.getTree(id)
}

package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.TreeRepository

class UpdateTreeUseCase(
    private val repository: TreeRepository,
) {
    suspend operator fun invoke(tree: GenogramTree) {
        repository.updateTree(tree)
    }
}

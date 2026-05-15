package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.TreeRepository

class GetTreesUseCase(
    private val repository: TreeRepository,
) {
    suspend operator fun invoke(): List<GenogramTree> = repository.getTrees()
}

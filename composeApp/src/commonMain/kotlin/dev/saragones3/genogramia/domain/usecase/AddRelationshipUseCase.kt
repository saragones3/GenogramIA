package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.repository.TreeRepository

class AddRelationshipUseCase(
    private val repository: TreeRepository,
) {
    suspend operator fun invoke(
        treeId: String,
        relationship: Relationship,
    ) {
        val tree = repository.getTree(treeId) ?: return
        val updatedRelationships = tree.relationships + relationship
        repository.updateTree(tree.copy(relationships = updatedRelationships))
    }
}

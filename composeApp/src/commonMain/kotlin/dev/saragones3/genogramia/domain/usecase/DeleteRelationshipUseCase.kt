package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.repository.TreeRepository

class DeleteRelationshipUseCase(
    private val repository: TreeRepository,
) {
    suspend operator fun invoke(
        treeId: String,
        relationshipId: String,
    ) {
        val tree = repository.getTree(treeId) ?: return
        val updatedRelationships = tree.relationships.filter { it.id != relationshipId }
        val updatedTree = tree.copy(relationships = updatedRelationships)
        repository.updateTree(updatedTree.copy(ancestorCount = updatedTree.calculateAncestorCount()))
    }
}

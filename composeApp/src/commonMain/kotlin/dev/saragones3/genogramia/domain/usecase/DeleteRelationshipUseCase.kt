package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.TreeRepository
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider

class DeleteRelationshipUseCase(
    private val repository: TreeRepository,
    private val dateProvider: DateProvider,
    private val dateFormatter: DateFormatter,
) {
    suspend operator fun invoke(
        treeId: String,
        relationshipId: String,
    ) {
        val tree = repository.getTree(treeId) ?: return
        val updatedRelationships = tree.relationships.filter { it.id != relationshipId }
        val updatedTree =
            tree.copy(
                relationships = updatedRelationships,
                lastUpdated =
                    dateFormatter.formatDate(
                        millis = dateProvider.nowEpochMilliseconds(),
                        pattern = GenogramTree.DATE_FORMAT,
                    ),
            )
        repository.updateTree(updatedTree.copy(ancestorCount = updatedTree.calculateAncestorCount()))
    }
}

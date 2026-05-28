package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.repository.TreeRepository
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider

class AddRelationshipUseCase(
    private val repository: TreeRepository,
    private val dateProvider: DateProvider,
    private val dateFormatter: DateFormatter,
) {
    suspend operator fun invoke(
        treeId: String,
        relationship: Relationship,
    ) {
        val tree = repository.getTree(treeId) ?: return
        val updatedTree =
            tree.copy(
                relationships = tree.relationships.filter { it.id != relationship.id } + relationship,
                lastUpdated =
                    dateFormatter.formatDate(
                        millis = dateProvider.nowEpochMilliseconds(),
                        pattern = GenogramTree.DATE_FORMAT,
                    ),
            )
        repository.updateTree(updatedTree.copy(ancestorCount = updatedTree.calculateAncestorCount()))
    }
}

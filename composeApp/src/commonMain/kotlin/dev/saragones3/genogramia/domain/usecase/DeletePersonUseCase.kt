package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.repository.TreeRepository

class DeletePersonUseCase(
    private val repository: TreeRepository,
) {
    enum class DeletePersonError {
        TREE_NOT_FOUND,
        HAS_DESCENDANTS,
        HAS_FORMAL_RELATIONSHIPS,
    }

    sealed class Result {
        object Success : Result()

        data class Error(
            val type: DeletePersonError,
        ) : Result()
    }

    suspend operator fun invoke(
        treeId: String,
        personId: String,
    ): Result {
        val tree = repository.getTree(treeId) ?: return Result.Error(DeletePersonError.TREE_NOT_FOUND)

        // 1. Check for descendants
        val hasDescendants =
            tree.relationships.any {
                (
                    it.type == Relationship.RelationshipType.BIOLOGICAL_OFFSPRING ||
                        it.type == Relationship.RelationshipType.ADOPTION_LEGAL
                ) &&
                    it.personId1 == personId
            }

        if (hasDescendants) {
            return Result.Error(DeletePersonError.HAS_DESCENDANTS)
        }

        // 2. Check for formal relationships
        val hasFormalRelationships =
            tree.relationships.any {
                (
                    it.type == Relationship.RelationshipType.MARRIAGE ||
                        it.type == Relationship.RelationshipType.SEPARATION ||
                        it.type == Relationship.RelationshipType.DIVORCE
                ) &&
                    (it.personId1 == personId || it.personId2 == personId)
            }

        if (hasFormalRelationships) {
            return Result.Error(DeletePersonError.HAS_FORMAL_RELATIONSHIPS)
        }

        // 3. Remove person and associated non-formal relationships (like cohabitation)
        val updatedPersons = tree.persons.filter { it.id != personId }
        val updatedRelationships =
            tree.relationships.filter {
                it.personId1 != personId && it.personId2 != personId
            }

        val updatedTree =
            tree.copy(
                persons = updatedPersons,
                relationships = updatedRelationships,
            )

        repository.updateTree(
            updatedTree.copy(
                ancestorCount = updatedTree.calculateAncestorCount(),
            ),
        )

        return Result.Success
    }
}

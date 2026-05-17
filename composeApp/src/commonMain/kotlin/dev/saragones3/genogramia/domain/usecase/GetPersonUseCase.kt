package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.repository.TreeRepository

class GetPersonUseCase(
    private val repository: TreeRepository,
) {
    suspend operator fun invoke(
        treeId: String,
        personId: String,
    ): Person? {
        val tree = repository.getTree(treeId) ?: return null
        return if (tree.centralPerson.id == personId) {
            tree.centralPerson
        } else {
            tree.persons.find { it.id == personId }
        }
    }
}

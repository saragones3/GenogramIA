package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.repository.TreeRepository
import dev.saragones3.genogramia.domain.util.DateProvider

class AddPersonUseCase(
    private val repository: TreeRepository,
    private val dateProvider: DateProvider,
) {
    suspend operator fun invoke(
        treeId: String,
        person: Person,
    ): Result<Unit> {
        return try {
            val tree =
                repository.getTree(treeId)
                    ?: return Result.failure(Exception("Tree not found"))

            val personWithId = person.copy(id = dateProvider.nowEpochMilliseconds().toString())
            val updatedPersons = tree.persons + personWithId

            repository.updateTree(tree.copy(persons = updatedPersons))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

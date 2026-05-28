package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.repository.TreeRepository
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider

class UpdatePersonUseCase(
    private val repository: TreeRepository,
    private val dateProvider: DateProvider,
    private val dateFormatter: DateFormatter,
) {
    suspend operator fun invoke(
        treeId: String,
        person: Person,
    ): Result<Unit> {
        return try {
            val tree =
                repository.getTree(treeId)
                    ?: return Result.failure(Exception("Tree not found"))

            val updatedPersons =
                tree.persons.map {
                    if (it.id == person.id) person else it
                }
            val updatedCentralPerson = if (tree.centralPerson.id == person.id) person else tree.centralPerson

            repository.updateTree(
                tree.copy(
                    persons = updatedPersons,
                    centralPerson = updatedCentralPerson,
                    lastUpdated =
                        dateFormatter.formatDate(
                            millis = dateProvider.nowEpochMilliseconds(),
                            pattern = GenogramTree.DATE_FORMAT,
                        ),
                ),
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.repository.TreeRepository
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider

class NewTreeUseCase(
    private val repository: TreeRepository,
    private val dateProvider: DateProvider,
    private val dateFormatter: DateFormatter,
) {
    suspend operator fun invoke(person: Person): Result<GenogramTree> {
        if (person.firstName.isBlank() || person.lastName.isBlank()) {
            return Result.failure(Exception("First name and last name are required"))
        }

        val centralPerson = person.copy(id = dateProvider.nowEpochMilliseconds().toString())

        val newTree =
            GenogramTree(
                id = "tree-${dateProvider.nowEpochMilliseconds()}",
                name = "${centralPerson.firstName} ${centralPerson.lastName} Lineage",
                ancestorCount = 1,
                lastUpdated =
                    dateFormatter.formatDate(
                        millis = dateProvider.nowEpochMilliseconds(),
                        pattern = "yyyy-MM-dd'T'HH:mm:ss",
                    ),
                centralPerson = centralPerson,
            )

        return try {
            Result.success(repository.createTree(newTree))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

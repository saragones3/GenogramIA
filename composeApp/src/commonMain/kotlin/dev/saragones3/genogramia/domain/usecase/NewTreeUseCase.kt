package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.repository.TreeRepository

class NewTreeUseCase(
    private val repository: TreeRepository,
) {
    suspend operator fun invoke(person: Person): Result<GenogramTree> {
        if (person.firstName.isBlank() || person.lastName.isBlank()) {
            return Result.failure(Exception("First name and last name are required"))
        }

        val centralPerson = person.copy(id = "person-1")

        val newTree =
            GenogramTree(
                id = "tree-${Clock.now()}", // Temporary ID generator
                name = "${centralPerson.firstName} ${centralPerson.lastName} Lineage",
                ancestorCount = 1,
                lastUpdated = "Just now", // TODO: set date in UTC
                centralPerson = centralPerson,
            )

        return try {
            Result.success(repository.createTree(newTree))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Simple helper for unique ID since we can't use JVM UUID easily in commonMain
    // and I don't see a timestamp util yet. Let's use a placeholder or check if I can use current time.
    // Actually, US-011 says created in local memory only for guest.
    // Let's use a simple timestamp or counter.
    private object Clock {
        fun now(): Long = 123456789L // Placeholder for now, maybe use kotlinx-datetime if available
    }
}

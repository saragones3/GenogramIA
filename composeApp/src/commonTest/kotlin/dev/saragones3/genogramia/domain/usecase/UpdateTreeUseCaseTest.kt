package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateTreeUseCaseTest {
    private val repository = FakeTreeRepository()
    private lateinit var useCase: UpdateTreeUseCase

    private val person1 = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
    private val tree =
        GenogramTree(
            id = "tree-1",
            name = "Test Tree",
            ancestorCount = 0,
            lastUpdated = "2024-05-15",
            centralPerson = person1,
        )

    @BeforeTest
    fun setup() {
        useCase = UpdateTreeUseCase(repository)
    }

    @Test
    fun `invoke should update the tree in repository`() =
        runTest {
            repository.createTree(tree)

            val updatedTree = tree.copy(name = "Updated Name")
            useCase(updatedTree)

            val savedTree = repository.getTree("tree-1")
            assertEquals("Updated Name", savedTree?.name)
        }
}

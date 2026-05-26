package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.fakes.FakeDateProvider
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddPersonUseCaseTest {
    private lateinit var repository: FakeTreeRepository
    private val fakeDateProvider =
        FakeDateProvider().apply {
            currentTimeMillis = 1778716800000L
        }
    private lateinit var useCase: AddPersonUseCase

    private val centralPerson = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
    private val tree =
        GenogramTree(
            id = "tree-1",
            name = "John Doe Lineage",
            ancestorCount = 0,
            lastUpdated = "2024-05-15",
            centralPerson = centralPerson,
        )

    @BeforeTest
    fun setup() {
        repository = FakeTreeRepository()
        useCase = AddPersonUseCase(repository, fakeDateProvider)
    }

    @Test
    fun `when tree exists person is added successfully`() =
        runTest {
            repository.createTree(tree)
            val newPerson =
                Person(
                    id = "",
                    firstName = "Jane",
                    lastName = "Doe",
                    birthDate = 0L,
                )

            val result = useCase("tree-1", newPerson)

            assertTrue(result.isSuccess)
            val updatedTree = repository.getTree("tree-1")
            assertEquals(1, updatedTree?.persons?.size)
            assertEquals("1778716800000", updatedTree?.persons?.get(0)?.id)
            assertEquals("Jane", updatedTree?.persons?.get(0)?.firstName)
        }

    @Test
    fun `when tree does not exist fails`() =
        runTest {
            val newPerson = Person(id = "", firstName = "Jane", lastName = "Doe", birthDate = 0L)

            val result = useCase("invalid-tree", newPerson)

            assertTrue(result.isFailure)
            assertEquals("Tree not found", result.exceptionOrNull()?.message)
        }

    @Test
    fun `when repository fails fails`() =
        runTest {
            repository.shouldReturnError = true
            val newPerson = Person(id = "", firstName = "Jane", lastName = "Doe", birthDate = 0L)

            val result = useCase("tree-1", newPerson)

            assertTrue(result.isFailure)
        }
}

package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.util.DateFormatter
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
    private val dateFormatter = DateFormatter()
    private lateinit var useCase: AddPersonUseCase

    private val centralPerson = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
    private val tree =
        GenogramTree(
            id = "tree-1",
            ancestorCount = 0,
            lastUpdated = "2024-05-15",
            centralPerson = centralPerson,
        )

    @BeforeTest
    fun setup() {
        repository = FakeTreeRepository()
        useCase = AddPersonUseCase(repository, fakeDateProvider, dateFormatter)
    }

    @Test
    fun `GIVEN existing tree WHEN adding person THEN person is added and last updated is updated`() =
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
            assertEquals("2026-05-14T00:00:00", updatedTree?.lastUpdated)
            assertEquals(0, updatedTree?.ancestorCount)
        }

    @Test
    fun `GIVEN non-existing tree WHEN adding person THEN returns failure`() =
        runTest {
            val newPerson = Person(id = "", firstName = "Jane", lastName = "Doe", birthDate = 0L)

            val result = useCase("invalid-tree", newPerson)

            assertTrue(result.isFailure)
            assertEquals("Tree not found", result.exceptionOrNull()?.message)
        }

    @Test
    fun `GIVEN repository error WHEN adding person THEN returns failure`() =
        runTest {
            repository.shouldReturnError = true
            val newPerson = Person(id = "", firstName = "Jane", lastName = "Doe", birthDate = 0L)

            val result = useCase("tree-1", newPerson)

            assertTrue(result.isFailure)
        }
}

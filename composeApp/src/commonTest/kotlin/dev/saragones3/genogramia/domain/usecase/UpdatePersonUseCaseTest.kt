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

class UpdatePersonUseCaseTest {
    private lateinit var repository: FakeTreeRepository
    private val fakeDateProvider = FakeDateProvider().apply { currentTimeMillis = 1778716800000L } // 14-may-2026
    private val dateFormatter = DateFormatter()
    private lateinit var useCase: UpdatePersonUseCase

    private val centralPerson = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
    private val otherPerson = Person(id = "p2", firstName = "Jane", lastName = "Doe", birthDate = 0L)
    private val tree =
        GenogramTree(
            id = "tree-1",
            ancestorCount = 0,
            lastUpdated = "2024-05-15",
            centralPerson = centralPerson,
            persons = listOf(otherPerson),
        )

    @BeforeTest
    fun setup() {
        repository = FakeTreeRepository()
        useCase = UpdatePersonUseCase(repository, fakeDateProvider, dateFormatter)
    }

    @Test
    fun `when person exists in persons list is updated successfully and lastUpdated is updated`() =
        runTest {
            repository.createTree(tree)
            val updatedPerson = otherPerson.copy(firstName = "Jane Updated")

            val result = useCase("tree-1", updatedPerson)

            assertTrue(result.isSuccess)
            val updatedTree = repository.getTree("tree-1")
            assertEquals("Jane Updated", updatedTree?.persons?.find { it.id == "p2" }?.firstName)
            assertEquals("2026-05-14T00:00:00", updatedTree?.lastUpdated)
        }

    @Test
    fun `when person is central person is updated successfully`() =
        runTest {
            repository.createTree(tree)
            val updatedPerson = centralPerson.copy(firstName = "John Updated")

            val result = useCase("tree-1", updatedPerson)

            assertTrue(result.isSuccess)
            val updatedTree = repository.getTree("tree-1")
            assertEquals("John Updated", updatedTree?.centralPerson?.firstName)
        }

    @Test
    fun `when tree does not exist fails`() =
        runTest {
            val result = useCase("invalid-tree", otherPerson)

            assertTrue(result.isFailure)
            assertEquals("Tree not found", result.exceptionOrNull()?.message)
        }
}

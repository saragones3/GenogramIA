package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetPersonUseCaseTest {
    private lateinit var repository: FakeTreeRepository
    private lateinit var useCase: GetPersonUseCase

    private val centralPerson = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
    private val otherPerson = Person(id = "p2", firstName = "Jane", lastName = "Doe", birthDate = 0L)
    private val tree =
        GenogramTree(
            id = "tree-1",
            name = "John Doe Lineage",
            ancestorCount = 2,
            lastUpdated = "2024-05-15",
            centralPerson = centralPerson,
            persons = listOf(otherPerson),
        )

    @BeforeTest
    fun setup() {
        repository = FakeTreeRepository()
        useCase = GetPersonUseCase(repository)
    }

    @Test
    fun `when person exists in persons list is returned`() =
        runTest {
            repository.createTree(tree)
            val result = useCase("tree-1", "p2")
            assertEquals(otherPerson, result)
        }

    @Test
    fun `when person is central person is returned`() =
        runTest {
            repository.createTree(tree)
            val result = useCase("tree-1", "p1")
            assertEquals(centralPerson, result)
        }

    @Test
    fun `when person does not exist returns null`() =
        runTest {
            repository.createTree(tree)
            val result = useCase("tree-1", "invalid-p")
            assertNull(result)
        }

    @Test
    fun `when tree does not exist returns null`() =
        runTest {
            val result = useCase("invalid-tree", "p1")
            assertNull(result)
        }
}

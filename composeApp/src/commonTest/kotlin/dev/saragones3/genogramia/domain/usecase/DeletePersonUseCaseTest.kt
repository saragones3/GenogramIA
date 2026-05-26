package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeletePersonUseCaseTest {
    private lateinit var repository: FakeTreeRepository
    private lateinit var useCase: DeletePersonUseCase

    private val centralPerson = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
    private val otherPerson = Person(id = "p2", firstName = "Jane", lastName = "Smith", birthDate = 0L)
    private val childPerson = Person(id = "p3", firstName = "Baby", lastName = "Doe", birthDate = 0L)

    private val tree =
        GenogramTree(
            id = "tree-1",
            name = "Test Tree",
            ancestorCount = 0,
            lastUpdated = "2024-05-15",
            centralPerson = centralPerson,
            persons = listOf(centralPerson, otherPerson, childPerson),
        )

    @BeforeTest
    fun setup() {
        repository = FakeTreeRepository()
        useCase = DeletePersonUseCase(repository)
    }

    @Test
    fun `when person is deleted successfully`() =
        runTest {
            repository.createTree(tree)

            val result = useCase("tree-1", "p2")

            assertTrue(result is DeletePersonUseCase.Result.Success)
            val updatedTree = repository.getTree("tree-1")
            assertEquals(2, updatedTree?.persons?.size)
            assertEquals(updatedTree?.persons?.none { it.id == "p2" }, true)
        }

    @Test
    fun `when tree not found returns error`() =
        runTest {
            val result = useCase("invalid", "p1")
            assertTrue(result is DeletePersonUseCase.Result.Error)
            assertEquals(DeletePersonUseCase.DeletePersonError.TREE_NOT_FOUND, result.type)
        }

    @Test
    fun `when person has descendants returns error`() =
        runTest {
            val treeWithChild =
                tree.copy(
                    relationships =
                        listOf(
                            Relationship(
                                id = "r1",
                                personId1 = "p2",
                                personId2 = "p3",
                                type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                            ),
                        ),
                )
            repository.createTree(treeWithChild)

            val result = useCase("tree-1", "p2")

            assertTrue(result is DeletePersonUseCase.Result.Error)
            assertEquals(DeletePersonUseCase.DeletePersonError.HAS_DESCENDANTS, result.type)
        }

    @Test
    fun `when person has marriage returns error`() =
        runTest {
            val treeWithMarriage =
                tree.copy(
                    relationships =
                        listOf(
                            Relationship(
                                id = "r1",
                                personId1 = "p1",
                                personId2 = "p2",
                                type = Relationship.RelationshipType.MARRIAGE,
                            ),
                        ),
                )
            repository.createTree(treeWithMarriage)

            val result = useCase("tree-1", "p2")

            assertTrue(result is DeletePersonUseCase.Result.Error)
            assertEquals(DeletePersonUseCase.DeletePersonError.HAS_FORMAL_RELATIONSHIPS, result.type)
        }

    @Test
    fun `when person has cohabitation relationship it is removed during deletion`() =
        runTest {
            val treeWithCohabitation =
                tree.copy(
                    relationships =
                        listOf(
                            Relationship(
                                id = "r1",
                                personId1 = "p1",
                                personId2 = "p2",
                                type = Relationship.RelationshipType.COHABITATION,
                            ),
                        ),
                )
            repository.createTree(treeWithCohabitation)

            val result = useCase("tree-1", "p2")

            assertTrue(result is DeletePersonUseCase.Result.Success)
            val updatedTree = repository.getTree("tree-1")
            assertEquals(updatedTree?.relationships?.isEmpty(), true)
        }
}

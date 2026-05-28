package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.fakes.FakeDateProvider
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteRelationshipUseCaseTest {
    private val repository = FakeTreeRepository()
    private val fakeDateProvider = FakeDateProvider().apply { currentTimeMillis = 1778716800000L } // 14-may-2026
    private val dateFormatter = DateFormatter()
    private lateinit var useCase: DeleteRelationshipUseCase

    private val person1 = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
    private val person2 = Person(id = "p2", firstName = "Jane", lastName = "Smith", birthDate = 0L)
    private val relationship =
        Relationship(
            id = "rel-1",
            personId1 = "p1",
            personId2 = "p2",
            type = Relationship.RelationshipType.MARRIAGE,
        )
    private val tree =
        GenogramTree(
            id = "tree-1",
            ancestorCount = 2,
            lastUpdated = "2024-05-15",
            centralPerson = person1,
            persons = listOf(person2),
            relationships = listOf(relationship),
        )

    @BeforeTest
    fun setup() {
        useCase = DeleteRelationshipUseCase(repository, fakeDateProvider, dateFormatter)
    }

    @Test
    fun `GIVEN existing relationship WHEN delete relationship called THEN relationship is removed`() =
        runTest {
            repository.createTree(tree)

            useCase(treeId = "tree-1", relationshipId = "rel-1")

            val updatedTree = repository.getTree("tree-1")
            assertEquals(true, updatedTree?.relationships?.isEmpty())
            assertEquals("2026-05-14T00:00:00", updatedTree?.lastUpdated)
        }

    @Test
    fun `GIVEN non-existing tree WHEN delete relationship called THEN nothing happens`() =
        runTest {
            useCase(treeId = "invalid", relationshipId = "rel-1")
            // Should not throw exception
        }

    @Test
    fun `GIVEN multiple relationships WHEN delete one THEN others remain`() =
        runTest {
            val rel2 = relationship.copy(id = "rel-2")
            val treeWithTwoRels = tree.copy(relationships = listOf(relationship, rel2))
            repository.createTree(treeWithTwoRels)

            useCase(treeId = "tree-1", relationshipId = "rel-1")

            val updatedTree = repository.getTree("tree-1")
            assertEquals(1, updatedTree?.relationships?.size)
            assertEquals("rel-2", updatedTree?.relationships?.first()?.id)
        }

    @Test
    fun `GIVEN vertical relationship WHEN deleted THEN ancestor count is updated`() =
        runTest {
            val father = Person(id = "f", firstName = "Father", lastName = "Doe", birthDate = 0L)
            val rel =
                Relationship(
                    id = "rel-v",
                    personId1 = "f",
                    personId2 = "p1",
                    type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                )
            val treeWithAncestor =
                tree.copy(
                    persons = tree.persons + father,
                    relationships = listOf(rel),
                    ancestorCount = 1,
                )
            repository.createTree(treeWithAncestor)

            useCase(treeId = "tree-1", relationshipId = "rel-v")

            val updatedTree = repository.getTree("tree-1")
            assertEquals(0, updatedTree?.ancestorCount)
        }
}

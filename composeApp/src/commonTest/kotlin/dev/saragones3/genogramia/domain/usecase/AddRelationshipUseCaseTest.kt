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

class AddRelationshipUseCaseTest {
    private lateinit var repository: FakeTreeRepository
    private val fakeDateProvider = FakeDateProvider().apply { currentTimeMillis = 1778716800000L } // 14-may-2026
    private val dateFormatter = DateFormatter()
    private lateinit var useCase: AddRelationshipUseCase

    private val centralPerson = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
    private val tree =
        GenogramTree(
            id = "tree-1",
            ancestorCount = 1,
            lastUpdated = "2024-05-15",
            centralPerson = centralPerson,
        )

    @BeforeTest
    fun setup() {
        repository = FakeTreeRepository()
        useCase = AddRelationshipUseCase(repository, fakeDateProvider, dateFormatter)
    }

    @Test
    fun `GIVEN existing tree WHEN adding relationship THEN relationship is added and last updated is updated`() =
        runTest {
            repository.createTree(tree)
            val relationship =
                Relationship(
                    id = "rel-1",
                    personId1 = "p1",
                    personId2 = "p2",
                    type = Relationship.RelationshipType.MARRIAGE,
                )

            useCase("tree-1", relationship)

            val updatedTree = repository.getTree("tree-1")
            assertEquals(1, updatedTree?.relationships?.size)
            assertEquals("rel-1", updatedTree?.relationships?.get(0)?.id)
            assertEquals(Relationship.RelationshipType.MARRIAGE, updatedTree?.relationships?.get(0)?.type)
            assertEquals("2026-05-14T00:00:00", updatedTree?.lastUpdated)
        }

    @Test
    fun `GIVEN non-existing tree WHEN adding relationship THEN nothing is added`() =
        runTest {
            val relationship =
                Relationship(
                    id = "rel-1",
                    personId1 = "p1",
                    personId2 = "p2",
                    type = Relationship.RelationshipType.MARRIAGE,
                )

            useCase("invalid-tree", relationship)

            val updatedTree = repository.getTree("invalid-tree")
            assertEquals(null, updatedTree)
        }

    @Test
    fun `GIVEN existing relationship ID WHEN adding relationship THEN relationship is updated`() =
        runTest {
            val existingRel =
                Relationship(
                    id = "rel-1",
                    personId1 = "p1",
                    personId2 = "p2",
                    type = Relationship.RelationshipType.MARRIAGE,
                )
            repository.createTree(tree.copy(relationships = listOf(existingRel)))

            val updatedRel = existingRel.copy(type = Relationship.RelationshipType.DIVORCE)

            useCase("tree-1", updatedRel)

            val updatedTree = repository.getTree("tree-1")
            assertEquals(1, updatedTree?.relationships?.size)
            assertEquals(Relationship.RelationshipType.DIVORCE, updatedTree?.relationships?.get(0)?.type)
        }

    @Test
    fun `GIVEN biological offspring relationship WHEN adding relationship THEN ancestor count is updated`() =
        runTest {
            repository.createTree(tree)
            val parent = Person(id = "p2", firstName = "Father", lastName = "Doe", birthDate = 0L)
            val updatedTreeWithParent = tree.copy(persons = tree.persons + parent)
            repository.updateTree(updatedTreeWithParent)

            val relationship =
                Relationship(
                    id = "rel-1",
                    personId1 = "p2",
                    personId2 = "p1",
                    type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                )

            useCase("tree-1", relationship)

            val finalTree = repository.getTree("tree-1")
            assertEquals(1, finalTree?.ancestorCount)
        }
}

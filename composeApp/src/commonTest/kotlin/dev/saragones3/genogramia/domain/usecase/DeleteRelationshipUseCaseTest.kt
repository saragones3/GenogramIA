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

class DeleteRelationshipUseCaseTest {
    private val repository = FakeTreeRepository()
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
            name = "Test Tree",
            ancestorCount = 2,
            lastUpdated = "2024-05-15",
            centralPerson = person1,
            persons = listOf(person2),
            relationships = listOf(relationship),
        )

    @BeforeTest
    fun setup() {
        useCase = DeleteRelationshipUseCase(repository)
    }

    @Test
    fun `invoke should remove the relationship from the tree`() =
        runTest {
            repository.createTree(tree)

            useCase(treeId = "tree-1", relationshipId = "rel-1")

            val updatedTree = repository.getTree("tree-1")
            assertTrue(updatedTree?.relationships?.isEmpty() ?: false)
        }

    @Test
    fun `invoke should not fail if tree does not exist`() =
        runTest {
            useCase(treeId = "invalid", relationshipId = "rel-1")
            // Should not throw exception
        }

    @Test
    fun `invoke should not change other relationships`() =
        runTest {
            val rel2 = relationship.copy(id = "rel-2")
            val treeWithTwoRels = tree.copy(relationships = listOf(relationship, rel2))
            repository.createTree(treeWithTwoRels)

            useCase(treeId = "tree-1", relationshipId = "rel-1")

            val updatedTree = repository.getTree("tree-1")
            assertEquals(1, updatedTree?.relationships?.size)
            assertEquals("rel-2", updatedTree?.relationships?.first()?.id)
        }
}

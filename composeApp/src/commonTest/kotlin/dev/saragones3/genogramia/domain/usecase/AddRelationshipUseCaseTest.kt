package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddRelationshipUseCaseTest {
    private lateinit var repository: FakeTreeRepository
    private lateinit var useCase: AddRelationshipUseCase

    private val centralPerson = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
    private val tree =
        GenogramTree(
            id = "tree-1",
            name = "John Doe Lineage",
            ancestorCount = 1,
            lastUpdated = "2024-05-15",
            centralPerson = centralPerson,
        )

    @BeforeTest
    fun setup() {
        repository = FakeTreeRepository()
        useCase = AddRelationshipUseCase(repository)
    }

    @Test
    fun `when tree exists relationship is added successfully`() =
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
        }

    @Test
    fun `when tree does not exist nothing is added`() =
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
}

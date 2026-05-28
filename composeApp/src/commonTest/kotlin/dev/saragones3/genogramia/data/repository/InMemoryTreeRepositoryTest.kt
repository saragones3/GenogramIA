package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryTreeRepositoryTest {
    private val repository = InMemoryTreeRepository()

    private val centralPerson = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)

    @Test
    fun `GIVEN empty repository WHEN creating tree THEN tree is added`() =
        runTest {
            val tree =
                GenogramTree(
                    id = "1",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            repository.createTree(tree)

            val trees = repository.getTrees()
            assertEquals(1, trees.size)
            assertEquals(tree, trees[0])
        }

    @Test
    fun `GIVEN multiple trees WHEN getting tree by id THEN returns correct tree`() =
        runTest {
            val tree1 =
                GenogramTree(
                    id = "1",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            val tree2 =
                GenogramTree(
                    id = "2",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            repository.createTree(tree1)
            repository.createTree(tree2)

            val found = repository.getTree("2")
            assertEquals(tree2, found)
        }

    @Test
    fun `GIVEN multiple trees WHEN getting all trees THEN returns all trees`() =
        runTest {
            val tree1 =
                GenogramTree(
                    id = "1",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            val tree2 =
                GenogramTree(
                    id = "2",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            repository.createTree(tree1)
            repository.createTree(tree2)

            val all = repository.getTrees()
            assertEquals(2, all.size)
            assertTrue(all.contains(tree1))
            assertTrue(all.contains(tree2))
        }

    @Test
    fun `GIVEN existing tree WHEN updating tree THEN tree is updated`() =
        runTest {
            val tree =
                GenogramTree(
                    id = "1",
                    ancestorCount = 1,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            repository.createTree(tree)

            val updatedTree = tree.copy(centralPerson = tree.centralPerson.copy(lastName = "New Name"))
            repository.updateTree(updatedTree)

            val found = repository.getTree("1")
            assertEquals("New Name", found?.name)
        }

    @Test
    fun `GIVEN non-existing tree WHEN updating tree THEN tree is added`() =
        runTest {
            val tree =
                GenogramTree(
                    id = "1",
                    ancestorCount = 1,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            repository.updateTree(tree)

            val found = repository.getTree("1")
            assertEquals(tree, found)
        }
}

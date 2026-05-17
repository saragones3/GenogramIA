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
    fun `createTree should add tree to the list`() =
        runTest {
            val tree =
                GenogramTree(
                    id = "1",
                    name = "Test Tree",
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
    fun `getTree should return the correct tree`() =
        runTest {
            val tree1 =
                GenogramTree(
                    id = "1",
                    name = "Tree 1",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            val tree2 =
                GenogramTree(
                    id = "2",
                    name = "Tree 2",
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
    fun `getTrees should return all trees`() =
        runTest {
            val tree1 =
                GenogramTree(
                    id = "1",
                    name = "Tree 1",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            val tree2 =
                GenogramTree(
                    id = "2",
                    name = "Tree 2",
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
    fun `updateTree should update existing tree`() =
        runTest {
            val tree =
                GenogramTree(
                    id = "1",
                    name = "Old Name",
                    ancestorCount = 1,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            repository.createTree(tree)

            val updatedTree = tree.copy(name = "New Name")
            repository.updateTree(updatedTree)

            val found = repository.getTree("1")
            assertEquals("New Name", found?.name)
        }

    @Test
    fun `updateTree should add tree if it does not exist`() =
        runTest {
            val tree =
                GenogramTree(
                    id = "1",
                    name = "New Tree",
                    ancestorCount = 1,
                    lastUpdated = "2024-05-15",
                    centralPerson = centralPerson,
                )
            repository.updateTree(tree)

            val found = repository.getTree("1")
            assertEquals(tree, found)
        }
}

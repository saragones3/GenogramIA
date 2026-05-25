package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.data.firebase.AuthUser
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.fakes.FakeFirebaseProvider
import dev.saragones3.genogramia.fakes.FakeFirestoreProvider
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FirestoreTreeRepositoryTest {
    private lateinit var fakeFirestore: FakeFirestoreProvider
    private lateinit var fakeFirebase: FakeFirebaseProvider
    private lateinit var repository: FirestoreTreeRepository

    @BeforeTest
    fun setup() {
        fakeFirestore = FakeFirestoreProvider()
        fakeFirebase = FakeFirebaseProvider()
        repository = FirestoreTreeRepository(fakeFirestore, fakeFirebase)
    }

    @Test
    fun testCreateTreeSavesToFirestore() =
        runTest {
            val user = AuthUser("user123", "test@test.com", "Test")
            fakeFirebase.setCurrentUser(user)

            val tree =
                GenogramTree(
                    id = "tree1",
                    name = "My Tree",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = Person(id = "p1", firstName = "Central", lastName = "Person", birthDate = 0L),
                )

            repository.createTree(tree)

            val savedTree = fakeFirestore.database["user123"]?.get("tree1")
            assertNotNull(savedTree)
            assertEquals("My Tree", savedTree.name)
        }

    @Test
    fun testCreateTreeUnauthenticatedDoesNotSave() =
        runTest {
            fakeFirebase.setCurrentUser(null)

            val tree =
                GenogramTree(
                    id = "tree1",
                    name = "My Tree",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = Person(id = "p1", firstName = "Central", lastName = "Person", birthDate = 0L),
                )

            repository.createTree(tree)

            assertNull(fakeFirestore.database["user123"]?.get("tree1"))
        }

    @Test
    fun testGetTree() =
        runTest {
            val user = AuthUser("user123", "test@test.com", "Test")
            fakeFirebase.setCurrentUser(user)

            val tree =
                GenogramTree(
                    id = "tree1",
                    name = "My Tree",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = Person(id = "p1", firstName = "Central", lastName = "Person", birthDate = 0L),
                )
            repository.createTree(tree)

            val fetchedTree = repository.getTree("tree1")
            assertNotNull(fetchedTree)
            assertEquals("tree1", fetchedTree.id)
        }

    @Test
    fun testGetTrees() =
        runTest {
            val user = AuthUser("user123", "test@test.com", "Test")
            fakeFirebase.setCurrentUser(user)

            repository.createTree(
                GenogramTree(
                    id = "tree1",
                    name = "Tree 1",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = Person(id = "p1", firstName = "P1", lastName = "L1", birthDate = 0L),
                ),
            )
            repository.createTree(
                GenogramTree(
                    id = "tree2",
                    name = "Tree 2",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = Person(id = "p2", firstName = "P2", lastName = "L2", birthDate = 0L),
                ),
            )

            val trees = repository.getTrees()
            assertEquals(2, trees.size)
            assertTrue(trees.any { it.id == "tree1" })
            assertTrue(trees.any { it.id == "tree2" })
        }

    @Test
    fun testUpdateTree() =
        runTest {
            val user = AuthUser("user123", "test@test.com", "Test")
            fakeFirebase.setCurrentUser(user)

            val tree =
                GenogramTree(
                    id = "tree1",
                    name = "My Tree",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = Person(id = "p1", firstName = "Central", lastName = "Person", birthDate = 0L),
                )
            repository.createTree(tree)

            val updatedTree = tree.copy(name = "Updated Tree")
            repository.updateTree(updatedTree)

            val fetchedTree = repository.getTree("tree1")
            assertEquals("Updated Tree", fetchedTree?.name)
        }
}

package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.data.remote.model.AuthUser
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
    fun `GIVEN authenticated user WHEN creating tree THEN saves to firestore`() =
        runTest {
            val user = AuthUser("user123", "test@test.com", "Test")
            fakeFirebase.setCurrentUser(user)

            val tree =
                GenogramTree(
                    id = "tree1",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = Person(id = "p1", firstName = "Central", lastName = "Person", birthDate = 0L),
                )

            repository.createTree(tree)

            val savedTree = fakeFirestore.database["user123"]?.get("tree1")
            assertNotNull(savedTree)
        }

    @Test
    fun `GIVEN unauthenticated user WHEN creating tree THEN does not save`() =
        runTest {
            fakeFirebase.setCurrentUser(null)

            val tree =
                GenogramTree(
                    id = "tree1",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = Person(id = "p1", firstName = "Central", lastName = "Person", birthDate = 0L),
                )

            repository.createTree(tree)

            assertNull(fakeFirestore.database["user123"]?.get("tree1"))
        }

    @Test
    fun `GIVEN existing tree WHEN getting tree THEN returns tree`() =
        runTest {
            val user = AuthUser("user123", "test@test.com", "Test")
            fakeFirebase.setCurrentUser(user)

            val tree =
                GenogramTree(
                    id = "tree1",
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
    fun `GIVEN multiple trees WHEN getting trees THEN returns all trees`() =
        runTest {
            val user = AuthUser("user123", "test@test.com", "Test")
            fakeFirebase.setCurrentUser(user)

            repository.createTree(
                GenogramTree(
                    id = "tree1",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = Person(id = "p1", firstName = "P1", lastName = "L1", birthDate = 0L),
                ),
            )
            repository.createTree(
                GenogramTree(
                    id = "tree2",
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
    fun `GIVEN existing tree WHEN updating tree THEN tree is updated in firestore`() =
        runTest {
            val user = AuthUser("user123", "test@test.com", "Test")
            fakeFirebase.setCurrentUser(user)

            val tree =
                GenogramTree(
                    id = "tree1",
                    ancestorCount = 0,
                    lastUpdated = "2024-05-15",
                    centralPerson = Person(id = "p1", firstName = "Central", lastName = "Person", birthDate = 0L),
                )
            repository.createTree(tree)

            val updatedTree = tree.copy(centralPerson = tree.centralPerson.copy(lastName = "Updated"))
            repository.updateTree(updatedTree)

            val fetchedTree = repository.getTree("tree1")
            assertEquals("Updated", fetchedTree?.name)
        }
}

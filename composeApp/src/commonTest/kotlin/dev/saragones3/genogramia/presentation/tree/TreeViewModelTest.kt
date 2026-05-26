package dev.saragones3.genogramia.presentation.tree

import androidx.compose.ui.geometry.Offset
import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.usecase.DeletePersonUseCase
import dev.saragones3.genogramia.domain.usecase.DeleteTreeUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePersonUseCase
import dev.saragones3.genogramia.domain.usecase.UpdateTreeUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.fakes.FakeDateProvider
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TreeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val treeRepository = FakeTreeRepository()
    private val getTreeUseCase = GetTreeUseCase(treeRepository)
    private val dateFormatter = DateFormatter()
    private val dateProvider = FakeDateProvider()
    private val deletePersonUseCase = DeletePersonUseCase(treeRepository, dateProvider, dateFormatter)
    private val deleteTreeUseCase = DeleteTreeUseCase(treeRepository)
    private val updatePersonUseCase = UpdatePersonUseCase(treeRepository, dateProvider, dateFormatter)
    private val updateTreeUseCase = UpdateTreeUseCase(treeRepository, dateProvider, dateFormatter)
    private lateinit var viewModel: TreeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            TreeViewModel(
                getTreeUseCase,
                deletePersonUseCase,
                deleteTreeUseCase,
                updatePersonUseCase,
                updateTreeUseCase,
                dateFormatter,
                dateProvider,
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() =
        runTest {
            val state = viewModel.state.value
            assertEquals("", state.tree.id)
            assertEquals(0f, state.offset.x)
            assertEquals(0f, state.offset.y)
            assertEquals(1f, state.scale)
            assertEquals(false, state.isLoading)
        }

    @Test
    fun `when LoadTree event is received state is updated with tree data`() =
        runTest {
            val person =
                Person(
                    id = "p1",
                    firstName = "John",
                    lastName = "Doe",
                    birthDate = 315532800000L,
                    biologicalSex = Person.BiologicalSex.MALE,
                )
            val tree = GenogramTree("t1", "Family", 1, "now", person)
            treeRepository.createTree(tree)

            viewModel.state.test {
                assertEquals("", awaitItem().tree.id)

                viewModel.onEvent(TreeEvent.LoadTree("t1"))

                assertEquals(true, awaitItem().isLoading)

                val successState = awaitItem()
                assertEquals(false, successState.isLoading)
                assertEquals("t1", successState.tree.id)
                assertEquals(
                    "John Doe",
                    "${successState.tree.centralPerson.firstName} ${successState.tree.centralPerson.lastName}",
                )
                assertEquals("1980", successState.tree.centralPerson.birthDateText)
            }
        }

    @Test
    fun `when LoadTree fails state is updated with error and navigateBack`() =
        runTest {
            viewModel.state.test {
                assertEquals(null, awaitItem().error)

                viewModel.onEvent(TreeEvent.LoadTree("invalid"))

                assertEquals(true, awaitItem().isLoading)

                val errorState = awaitItem()
                assertEquals(TreeError.NOT_FOUND, errorState.error)
                assertEquals(true, errorState.shouldNavigateBack)
                assertEquals(false, errorState.isLoading)
            }
        }

    @Test
    fun `when LoadTree event is received with birth and death dates UI model is populated correctly`() =
        runTest {
            // 1915-01-01 and 1989-12-31 approx
            val person = Person("p1", "John", "Doe", birthDate = -1735689600000L, deathDate = 631065600000L)
            val tree = GenogramTree("t1", "Family", 1, "now", person)
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))

            testDispatcher.scheduler.advanceUntilIdle()

            val centralPerson = viewModel.state.value.tree.centralPerson
            assertEquals("1915", centralPerson.birthDateText)
            assertEquals("1989", centralPerson.deathDateText)
            assertEquals("74", centralPerson.age)
            assertEquals(true, centralPerson.isDeceased)
        }

    @Test
    fun `when person is alive age is calculated based on current date`() =
        runTest {
            // Set current date to 2024
            dateProvider.currentTimeMillis = 1704067200000L // 2024-01-01

            // 1980-01-01
            val person = Person("p1", "John", "Doe", birthDate = 315532800000L)
            val tree = GenogramTree("t1", "Family", 1, "now", person)
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))

            testDispatcher.scheduler.advanceUntilIdle()

            val centralPerson = viewModel.state.value.tree.centralPerson
            assertEquals("1980", centralPerson.birthDateText)
            assertEquals("", centralPerson.deathDateText)
            assertEquals("44", centralPerson.age)
            assertEquals(false, centralPerson.isDeceased)
        }

    @Test
    fun `when OnZoomIn event is received scale is increased`() =
        runTest {
            viewModel.onEvent(TreeEvent.OnZoomIn(0.2f))
            assertEquals(1.2f, viewModel.state.value.scale)
        }

    @Test
    fun `when OnZoomOut event is received scale is decreased`() =
        runTest {
            viewModel.onEvent(TreeEvent.OnZoomOut(0.2f))
            assertEquals(0.8f, viewModel.state.value.scale)
        }

    @Test
    fun `when OnResetViewport event is received state is reset`() =
        runTest {
            viewModel.onEvent(TreeEvent.OnZoomIn(0.5f))
            viewModel.onEvent(TreeEvent.OnResetViewport)

            val state = viewModel.state.value
            assertEquals(0f, state.offset.x)
            assertEquals(0f, state.offset.y)
            assertEquals(1f, state.scale)
        }

    @Test
    fun `when OnTransform event is received scale and offset are updated`() =
        runTest {
            val centroid = Offset.Zero
            val pan = Offset(10f, 20f)
            viewModel.onEvent(TreeEvent.OnTransform(centroid, pan, 1.1f))

            val state = viewModel.state.value
            assertEquals(10f, state.offset.x)
            assertEquals(20f, state.offset.y)
            assertEquals(1.1f, state.scale)
        }

    @Test
    fun `when LoadTree has multiple persons they are mapped to UI`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val p2 = Person("p2", "Jane", "Doe", 0L)
            val tree = GenogramTree("t1", "Family", 2, "now", central, listOf(p2))
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))

            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, viewModel.state.value.tree.persons.size)
            assertEquals(
                "Jane",
                viewModel.state.value.tree.persons[0]
                    .firstName,
            )
        }

    @Test
    fun `when LoadTree has relationships they are mapped to UI`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val p2 = Person("p2", "Jane", "Doe", 0L)
            val rel =
                Relationship(
                    id = "r1",
                    personId1 = "p1",
                    personId2 = "p2",
                    type = Relationship.RelationshipType.MARRIAGE,
                )
            val tree = GenogramTree("t1", "Family", 2, "now", central, listOf(p2), listOf(rel))
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))

            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, viewModel.state.value.tree.relationships.size)
            assertEquals(
                "r1",
                viewModel.state.value.tree.relationships[0]
                    .id,
            )
            assertEquals(
                Relationship.RelationshipType.MARRIAGE,
                viewModel.state.value.tree.relationships[0]
                    .type,
            )
        }

    @Test
    fun `when OnPersonSelected event is received selectedPersonIds is updated`() =
        runTest {
            viewModel.onEvent(TreeEvent.OnPersonSelected("p1"))
            assertEquals(listOf("p1"), viewModel.state.value.selectedPersonIds)
        }

    @Test
    fun `when two OnPersonSelected events are received for different persons both are selected`() =
        runTest {
            viewModel.onEvent(TreeEvent.OnPersonSelected("p1"))
            viewModel.onEvent(TreeEvent.OnPersonSelected("p2"))
            assertEquals(listOf("p1", "p2"), viewModel.state.value.selectedPersonIds)
        }

    @Test
    fun `when OnPersonSelected is called on already selected person it is deselected`() =
        runTest {
            viewModel.onEvent(TreeEvent.OnPersonSelected("p1"))
            viewModel.onEvent(TreeEvent.OnPersonSelected("p2"))
            viewModel.onEvent(TreeEvent.OnPersonSelected("p1"))
            assertEquals(listOf("p2"), viewModel.state.value.selectedPersonIds)
        }

    @Test
    fun `when third person is selected only the new one remains selected`() =
        runTest {
            viewModel.onEvent(TreeEvent.OnPersonSelected("p1"))
            viewModel.onEvent(TreeEvent.OnPersonSelected("p2"))
            viewModel.onEvent(TreeEvent.OnPersonSelected("p3"))
            assertEquals(listOf("p3"), viewModel.state.value.selectedPersonIds)
        }

    @Test
    fun `when two persons with existing relationship are selected relationshipId is set automatically`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val p2 = Person("p2", "Jane", "Doe", 0L)
            val rel =
                Relationship(
                    id = "r1",
                    personId1 = "p1",
                    personId2 = "p2",
                    type = Relationship.RelationshipType.MARRIAGE,
                )
            val tree = GenogramTree("t1", "Family", 2, "now", central, listOf(p2), listOf(rel))
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(TreeEvent.OnPersonSelected("p1"))
            viewModel.onEvent(TreeEvent.OnPersonSelected("p2"))

            assertEquals("r1", viewModel.state.value.selectedRelationshipId)
        }

    @Test
    fun `when two persons without relationship are selected relationshipId is null`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val p2 = Person("p2", "Jane", "Doe", 0L)
            val tree = GenogramTree("t1", "Family", 2, "now", central, listOf(p2))
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(TreeEvent.OnPersonSelected("p1"))
            viewModel.onEvent(TreeEvent.OnPersonSelected("p2"))

            assertEquals(null, viewModel.state.value.selectedRelationshipId)
        }

    @Test
    fun `when OnPersonMove event is received person position is updated`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val tree = GenogramTree("t1", "Family", 1, "now", central)
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val initialPos = viewModel.state.value.tree.centralPerson.position
            val delta = Offset(10f, 20f)
            viewModel.onEvent(TreeEvent.OnPersonMove("p1", delta))

            assertEquals(initialPos + delta, viewModel.state.value.tree.centralPerson.position)
        }

    @Test
    fun `when OnPersonMoveFinished event is received person position is saved in repository`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val tree = GenogramTree("t1", "Family", 1, "now", central)
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val delta = Offset(100f, 200f)
            viewModel.onEvent(TreeEvent.OnPersonMove("p1", delta))
            viewModel.onEvent(TreeEvent.OnPersonMoveFinished("p1"))

            testDispatcher.scheduler.advanceUntilIdle()

            val savedTree = treeRepository.getTree("t1")
            assertEquals(100f, savedTree?.centralPerson?.x)
            assertEquals(200f, savedTree?.centralPerson?.y)
        }

    @Test
    fun `age calculation handles birthday not yet reached in current year`() =
        runTest {
            // Current date: 2024-05-01
            dateProvider.currentTimeMillis = 1714521600000L

            // Birth date: 1980-06-01 (Birthday hasn't happened yet in 2024)
            val person = Person("p1", "John", "Doe", birthDate = 328665600000L)
            val tree = GenogramTree("t1", "Family", 1, "now", person)
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val centralPerson = viewModel.state.value.tree.centralPerson
            // 2024 - 1980 = 44, but since it's May and birthday is June, should be 43
            assertEquals("43", centralPerson.age)
        }

    @Test
    fun `age calculation handles birthday already reached in current year`() =
        runTest {
            // Current date: 2024-07-01
            dateProvider.currentTimeMillis = 1719792000000L

            // Birth date: 1980-06-01 (Birthday already happened in 2024)
            val person = Person("p1", "John", "Doe", birthDate = 328665600000L)
            val tree = GenogramTree("t1", "Family", 1, "now", person)
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val centralPerson = viewModel.state.value.tree.centralPerson
            assertEquals("44", centralPerson.age)
        }

    @Test
    fun `when LoadTree has adoption relationship adoptive parent is positioned as parent`() =
        runTest {
            val central = Person("child", "Child", "Doe", 0L)
            val adoptiveParent =
                Person(
                    id = "parent",
                    firstName = "Adoptive",
                    lastName = "Parent",
                    birthDate = 0L,
                    biologicalSex = Person.BiologicalSex.MALE,
                )
            val rel =
                Relationship(
                    id = "r1",
                    personId1 = "parent",
                    personId2 = "child",
                    type = Relationship.RelationshipType.ADOPTION_LEGAL,
                )
            val tree = GenogramTree("t1", "Family", 2, "now", central, listOf(adoptiveParent), listOf(rel))
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val mappedParent =
                viewModel.state.value.tree.persons
                    .find { it.id == "parent" }
            // Parents are positioned at y = -250f
            assertEquals(-250f, mappedParent?.position?.y)
        }

    @Test
    fun `when LoadTree has persons with 0 position they are baked into repository`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val p2 = Person("p2", "Partner", "Doe", 0L, biologicalSex = Person.BiologicalSex.FEMALE)
            val rel =
                Relationship(
                    id = "r1",
                    personId1 = "p1",
                    personId2 = "p2",
                    type = Relationship.RelationshipType.MARRIAGE,
                )
            // Both persons have 0,0 coordinates
            val tree = GenogramTree("t1", "Family", 2, "now", central, listOf(p2), listOf(rel))
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val savedTree = treeRepository.getTree("t1")
            // Partner should have been baked at Offset(250f, 0f) according to toUi() logic
            assertEquals(250f, savedTree?.persons?.find { it.id == "p2" }?.x)
            assertEquals(0f, savedTree?.persons?.find { it.id == "p2" }?.y)
        }

    @Test
    fun `when relationship is removed baked positions are preserved`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L, x = 100f, y = 100f)
            val p2 = Person("p2", "Ex-Partner", "Doe", 0L, x = 350f, y = 100f)
            // No relationships
            val tree = GenogramTree("t1", "Family", 2, "now", central, listOf(p2), emptyList())
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val uiP2 =
                viewModel.state.value.tree.persons
                    .find { it.id == "p2" }
            // Should still be at 350, 100 even without the relationship that originally placed it there
            assertEquals(350f, uiP2?.position?.x)
            assertEquals(100f, uiP2?.position?.y)
        }

    @Test
    fun `when OnDeleteSelectedPersonRequested event is received delete confirmation is shown`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val p2 = Person("p2", "Jane", "Doe", 0L)
            val tree = GenogramTree("t1", "Family", 2, "now", central, listOf(p2))
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(TreeEvent.OnPersonSelected("p2"))
            viewModel.onEvent(TreeEvent.OnDeleteSelectedPersonRequested)

            val state = viewModel.state.value
            assertEquals(true, state.showDeleteConfirmation)
            assertEquals("Jane Doe", state.personToDeleteName)
        }

    @Test
    fun `when OnDismissDeletePerson event is received delete confirmation is hidden`() =
        runTest {
            viewModel.onEvent(TreeEvent.OnPersonSelected("p2"))
            viewModel.onEvent(TreeEvent.OnDeleteSelectedPersonRequested)
            viewModel.onEvent(TreeEvent.OnDismissDeletePerson)

            val state = viewModel.state.value
            assertEquals(false, state.showDeleteConfirmation)
            assertEquals(null, state.personToDeleteName)
        }

    @Test
    fun `when OnConfirmDeletePerson event is received person is deleted and tree is reloaded`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val p2 = Person("p2", "Jane", "Doe", 0L)
            val tree = GenogramTree("t1", "Family", 2, "now", central, listOf(p2))
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(TreeEvent.OnPersonSelected("p2"))
            viewModel.onEvent(TreeEvent.OnConfirmDeletePerson)

            testDispatcher.scheduler.advanceUntilIdle()

            val savedTree = treeRepository.getTree("t1")
            assertEquals(0, savedTree?.persons?.size)
            assertEquals(false, viewModel.state.value.showDeleteConfirmation)
        }

    @Test
    fun `when OnConfirmDeletePerson fails error is set`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val p2 = Person("p2", "Jane", "Doe", 0L)
            // Create a marriage so it cannot be deleted
            val rel = Relationship("r1", "p1", "p2", Relationship.RelationshipType.MARRIAGE)
            val tree = GenogramTree("t1", "Family", 2, "now", central, listOf(p2), listOf(rel))
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(TreeEvent.OnPersonSelected("p2"))
            viewModel.onEvent(TreeEvent.OnConfirmDeletePerson)

            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                TreeError.HAS_FORMAL_RELATIONSHIPS,
                viewModel.state.value.error,
            )
            assertEquals(false, viewModel.state.value.isLoading)
        }

    @Test
    fun `when OnDeleteTreeRequested event is received showDeleteTreeConfirmation is true`() =
        runTest {
            viewModel.onEvent(TreeEvent.OnDeleteTreeRequested)
            assertEquals(true, viewModel.state.value.showDeleteTreeConfirmation)
        }

    @Test
    fun `when OnDismissDeleteTree event is received showDeleteTreeConfirmation is false`() =
        runTest {
            viewModel.onEvent(TreeEvent.OnDeleteTreeRequested)
            viewModel.onEvent(TreeEvent.OnDismissDeleteTree)
            assertEquals(false, viewModel.state.value.showDeleteTreeConfirmation)
        }

    @Test
    fun `when OnConfirmDeleteTree event is received tree is deleted and shouldNavigateBack is true`() =
        runTest {
            val central = Person("p1", "John", "Doe", 0L)
            val tree = GenogramTree("t1", "Family", 1, "now", central)
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(TreeEvent.OnDeleteTreeRequested)
            viewModel.onEvent(TreeEvent.OnConfirmDeleteTree)

            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(null, treeRepository.getTree("t1"))
            assertEquals(true, viewModel.state.value.shouldNavigateBack)
            assertEquals(false, viewModel.state.value.showDeleteTreeConfirmation)
        }
}

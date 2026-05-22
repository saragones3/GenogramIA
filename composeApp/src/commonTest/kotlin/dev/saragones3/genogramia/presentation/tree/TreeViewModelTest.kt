package dev.saragones3.genogramia.presentation.tree

import androidx.compose.ui.geometry.Offset
import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePersonUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
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
    private val updatePersonUseCase = UpdatePersonUseCase(treeRepository)
    private val dateFormatter = DateFormatter()
    private lateinit var viewModel: TreeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TreeViewModel(getTreeUseCase, updatePersonUseCase, dateFormatter)
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
    fun `when two persons with existing relationship are selected, relationshipId is set automatically`() =
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
    fun `when two persons without relationship are selected, relationshipId is null`() =
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
}

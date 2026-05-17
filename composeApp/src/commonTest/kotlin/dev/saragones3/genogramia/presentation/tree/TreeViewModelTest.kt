package dev.saragones3.genogramia.presentation.tree

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
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
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class TreeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val treeRepository = FakeTreeRepository()
    private val getTreeUseCase = GetTreeUseCase(treeRepository)
    private val dateFormatter = DateFormatter()
    private lateinit var viewModel: TreeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TreeViewModel(getTreeUseCase, dateFormatter)
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
            val person = Person("p1", "John", "Doe", Person.BiologicalSex.MALE, birthDate = 315532800000L)
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
            val pan =
                androidx.compose.ui.geometry
                    .Offset(10f, 20f)
            viewModel.onEvent(TreeEvent.OnTransform(pan, 1.1f))

            val state = viewModel.state.value
            assertEquals(10f, state.offset.x)
            assertEquals(20f, state.offset.y)
            assertEquals(1.1f, state.scale)
        }

    @Test
    fun `when LoadTree has multiple persons they are mapped to UI`() =
        runTest {
            val central = Person("p1", "John", "Doe")
            val p2 = Person("p2", "Jane", "Doe")
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
}

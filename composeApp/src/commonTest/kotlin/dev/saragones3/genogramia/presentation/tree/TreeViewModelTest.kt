package dev.saragones3.genogramia.presentation.tree

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
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
    private lateinit var viewModel: TreeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TreeViewModel(getTreeUseCase)
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
            val person = Person("p1", "John", "Doe", Person.BiologicalSex.MALE, birthDate = "01/01/1980")
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
                assertEquals("B. 1980", successState.tree.centralPerson.dateText)
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
    fun `when LoadTree event is received with birth and death dates dateText is formatted correctly`() =
        runTest {
            val person = Person("p1", "John", "Doe", birthDate = "01/01/1915", deathDate = "31/12/1989")
            val tree = GenogramTree("t1", "Family", 1, "now", person)
            treeRepository.createTree(tree)

            viewModel.onEvent(TreeEvent.LoadTree("t1"))

            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals("1915 - 1989", viewModel.state.value.tree.centralPerson.dateText)
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
}

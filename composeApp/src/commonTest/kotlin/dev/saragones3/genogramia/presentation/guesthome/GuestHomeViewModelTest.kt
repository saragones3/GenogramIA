package dev.saragones3.genogramia.presentation.guesthome

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.GetTreesUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.fakes.FakeDateProvider
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class GuestHomeViewModelTest {
    private lateinit var treeRepository: FakeTreeRepository
    private lateinit var getTreesUseCase: GetTreesUseCase
    private lateinit var viewModel: GuestHomeViewModel
    private val dateProvider = FakeDateProvider()
    private val dateFormatter = DateFormatter()

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        treeRepository = FakeTreeRepository()
        getTreesUseCase = GetTreesUseCase(treeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when view model is initialized trees should be loaded from repository`() =
        runTest {
            val tree = GenogramTree("1", "Sample Tree", 1, "2024-05-15T12:00:00", Person())
            treeRepository.createTree(tree)

            viewModel = GuestHomeViewModel(getTreesUseCase, dateProvider, dateFormatter)
            viewModel.onResume()

            assertEquals(1, viewModel.trees.value.size)
            assertEquals("Sample Tree", viewModel.trees.value[0].title)
        }

    @Test
    fun `when search query changes trees should be filtered`() =
        runTest {
            treeRepository.createTree(GenogramTree("1", "Sample Tree", 1, "2024-05-15T12:00:00", Person()))
            treeRepository.createTree(GenogramTree("2", "Another Tree", 1, "2024-05-15T12:00:00", Person()))

            viewModel = GuestHomeViewModel(getTreesUseCase, dateProvider, dateFormatter)
            viewModel.onResume()

            viewModel.onSearchQueryChange("Sample")
            assertEquals(1, viewModel.trees.value.size)
            assertEquals("Sample Tree", viewModel.trees.value[0].title)

            viewModel.onSearchQueryChange("NonExistent")
            assertEquals(0, viewModel.trees.value.size)
        }
}

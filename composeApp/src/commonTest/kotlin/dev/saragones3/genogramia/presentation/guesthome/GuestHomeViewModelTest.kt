package dev.saragones3.genogramia.presentation.guesthome

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.GetTreesUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.fakes.FakeDateProvider
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import dev.saragones3.genogramia.presentation.util.UiText
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.new_tree_name
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
            val tree =
                GenogramTree(
                    id = "1",
                    ancestorCount = 1,
                    lastUpdated = "2024-05-15T12:00:00",
                    centralPerson = Person(id = "p1", firstName = "John", lastName = "Sample Tree", birthDate = 0L),
                )
            treeRepository.createTree(tree)

            viewModel = GuestHomeViewModel(getTreesUseCase, dateProvider, dateFormatter)
            viewModel.onResume()

            assertEquals(1, viewModel.uiState.value.trees.size)
            assertEquals(
                UiText.Resource(Res.string.new_tree_name, arrayOf("Sample Tree")),
                viewModel.uiState.value.trees[0]
                    .title,
            )
        }

    @Test
    fun `when search query changes trees should be filtered`() =
        runTest {
            treeRepository.createTree(
                GenogramTree(
                    id = "1",
                    ancestorCount = 1,
                    lastUpdated = "2024-05-15T12:00:00",
                    centralPerson = Person(id = "p1", firstName = "John", lastName = "Sample Tree", birthDate = 0L),
                ),
            )
            treeRepository.createTree(
                GenogramTree(
                    id = "2",
                    ancestorCount = 1,
                    lastUpdated = "2024-05-15T12:00:00",
                    centralPerson = Person(id = "p2", firstName = "Jane", lastName = "Another Tree", birthDate = 0L),
                ),
            )

            viewModel = GuestHomeViewModel(getTreesUseCase, dateProvider, dateFormatter)
            viewModel.onResume()

            viewModel.onSearchQueryChange("Sample")
            assertEquals(1, viewModel.uiState.value.trees.size)
            assertEquals(
                UiText.Resource(Res.string.new_tree_name, arrayOf("Sample Tree")),
                viewModel.uiState.value.trees[0]
                    .title,
            )

            viewModel.onSearchQueryChange("NonExistent")
            assertEquals(0, viewModel.uiState.value.trees.size)
        }
}

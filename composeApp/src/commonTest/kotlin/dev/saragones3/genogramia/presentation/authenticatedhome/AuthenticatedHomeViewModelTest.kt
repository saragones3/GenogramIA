package dev.saragones3.genogramia.presentation.authenticatedhome

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreesUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.fakes.FakeAuthRepository
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticatedHomeViewModelTest {
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var treeRepository: FakeTreeRepository
    private lateinit var checkSessionUseCase: CheckSessionUseCase
    private lateinit var getTreesUseCase: GetTreesUseCase
    private lateinit var viewModel: AuthenticatedHomeViewModel
    private val dateProvider = FakeDateProvider()
    private val dateFormatter = DateFormatter()

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = FakeAuthRepository()
        treeRepository = FakeTreeRepository()
        checkSessionUseCase = CheckSessionUseCase(authRepository)
        getTreesUseCase = GetTreesUseCase(treeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when user is logged in userName state should have displayName`() =
        runTest {
            val user = User("123", "test@test.com", "John Doe")
            authRepository.setCurrentUser(user)

            viewModel = AuthenticatedHomeViewModel(checkSessionUseCase, getTreesUseCase, dateProvider, dateFormatter)

            assertTrue(viewModel.uiState.value.isLoading) // Initial state
            viewModel.onResume()

            assertEquals("John Doe", viewModel.uiState.value.userName)
            assertFalse(viewModel.uiState.value.isLoading) // After loading
        }

    @Test
    fun `when view model is initialized trees should be loaded from repository`() =
        runTest {
            val user = User("123", "test@test.com", "John Doe")
            authRepository.setCurrentUser(user)
            val tree =
                GenogramTree(
                    id = "1",
                    ancestorCount = 1,
                    lastUpdated = "2024-05-15T12:00:00",
                    centralPerson = Person(id = "p1", firstName = "John", lastName = "Smith Family", birthDate = 0L),
                )
            treeRepository.createTree(tree)

            viewModel = AuthenticatedHomeViewModel(checkSessionUseCase, getTreesUseCase, dateProvider, dateFormatter)

            viewModel.uiState.test {
                assertTrue(awaitItem().isLoading) // Initial true

                viewModel.onResume()

                val state = expectMostRecentItem()
                assertEquals(1, state.trees.size)
                assertEquals(
                    UiText.Resource(Res.string.new_tree_name, arrayOf("Smith Family")),
                    state.trees[0].title,
                )
                assertFalse(state.isLoading) // Finished loading
            }
        }

    @Test
    fun `when search query changes trees should be filtered`() =
        runTest {
            val user = User("123", "test@test.com", "John Doe")
            authRepository.setCurrentUser(user)
            treeRepository.createTree(
                GenogramTree(
                    id = "1",
                    ancestorCount = 1,
                    lastUpdated = "2024-05-15T12:00:00",
                    centralPerson = Person(id = "p1", firstName = "John", lastName = "Smith Family", birthDate = 0L),
                ),
            )
            treeRepository.createTree(
                GenogramTree(
                    id = "2",
                    ancestorCount = 1,
                    lastUpdated = "2024-05-15T12:00:00",
                    centralPerson =
                        Person(
                            id = "p2",
                            firstName = "Jane",
                            lastName = "Maternal Lineage",
                            birthDate = 0L,
                        ),
                ),
            )

            viewModel =
                AuthenticatedHomeViewModel(
                    checkSessionUseCase,
                    getTreesUseCase,
                    dateProvider,
                    dateFormatter,
                )
            viewModel.onResume()

            viewModel.onSearchQueryChange("Smith")
            assertEquals(1, viewModel.uiState.value.trees.size)
            assertEquals(
                UiText.Resource(Res.string.new_tree_name, arrayOf("Smith Family")),
                viewModel.uiState.value.trees[0]
                    .title,
            )

            viewModel.onSearchQueryChange("NonExistent")
            assertEquals(0, viewModel.uiState.value.trees.size)
        }

    @Test
    fun `when user is logged in without displayName userName state should be User`() =
        runTest {
            val user = User("123", "test@test.com", null)
            authRepository.setCurrentUser(user)

            viewModel = AuthenticatedHomeViewModel(checkSessionUseCase, getTreesUseCase, dateProvider, dateFormatter)
            viewModel.onResume()

            assertEquals("User", viewModel.uiState.value.userName)
        }
}

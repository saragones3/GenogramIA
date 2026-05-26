package dev.saragones3.genogramia.presentation.authenticatedhome

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreesUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.fakes.FakeAuthRepository
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
            viewModel.onResume()

            assertEquals("John Doe", viewModel.userName.value)
        }

    @Test
    fun `when view model is initialized trees should be loaded from repository`() =
        runTest {
            val user = User("123", "test@test.com", "John Doe")
            authRepository.setCurrentUser(user)
            val tree = GenogramTree("1", "Smith Family", 1, "2024-05-15T12:00:00", Person())
            treeRepository.createTree(tree)

            viewModel = AuthenticatedHomeViewModel(checkSessionUseCase, getTreesUseCase, dateProvider, dateFormatter)
            viewModel.onResume()

            assertEquals(1, viewModel.trees.value.size)
            assertEquals("Smith Family", viewModel.trees.value[0].title)
        }

    @Test
    fun `when search query changes trees should be filtered`() =
        runTest {
            val user = User("123", "test@test.com", "John Doe")
            authRepository.setCurrentUser(user)
            treeRepository.createTree(GenogramTree("1", "Smith Family", 1, "2024-05-15T12:00:00", Person()))
            treeRepository.createTree(GenogramTree("2", "Maternal Lineage", 1, "2024-05-15T12:00:00", Person()))

            viewModel = AuthenticatedHomeViewModel(checkSessionUseCase, getTreesUseCase, dateProvider, dateFormatter)
            viewModel.onResume()

            viewModel.onSearchQueryChange("Smith")
            assertEquals(1, viewModel.trees.value.size)
            assertEquals("Smith Family", viewModel.trees.value[0].title)

            viewModel.onSearchQueryChange("NonExistent")
            assertEquals(0, viewModel.trees.value.size)
        }

    @Test
    fun `when user is logged in without displayName userName state should be User`() =
        runTest {
            val user = User("123", "test@test.com", null)
            authRepository.setCurrentUser(user)

            viewModel = AuthenticatedHomeViewModel(checkSessionUseCase, getTreesUseCase, dateProvider, dateFormatter)
            viewModel.onResume()

            assertEquals("User", viewModel.userName.value)
        }
}

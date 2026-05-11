package dev.saragones3.genogramia.presentation.authenticatedhome

import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthenticatedHomeViewModelTest {
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var checkSessionUseCase: CheckSessionUseCase
    private lateinit var viewModel: AuthenticatedHomeViewModel

    @BeforeTest
    fun setup() {
        authRepository = FakeAuthRepository()
        checkSessionUseCase = CheckSessionUseCase(authRepository)
    }

    @Test
    fun `when user is logged in userName state should have displayName`() =
        runTest {
            val user = User("123", "test@test.com", "John Doe")
            authRepository.setCurrentUser(user)

            viewModel = AuthenticatedHomeViewModel(checkSessionUseCase)

            assertEquals("John Doe", viewModel.userName.value)
        }

    @Test
    fun `when view model is initialized trees should be loaded with mock data`() =
        runTest {
            val user = User("123", "test@test.com", "John Doe")
            authRepository.setCurrentUser(user)

            viewModel = AuthenticatedHomeViewModel(checkSessionUseCase)

            assertEquals(3, viewModel.trees.value.size)
            assertEquals("Smith Family", viewModel.trees.value[0].name)
        }

    @Test
    fun `when search query changes trees should be filtered`() =
        runTest {
            val user = User("123", "test@test.com", "John Doe")
            authRepository.setCurrentUser(user)

            viewModel = AuthenticatedHomeViewModel(checkSessionUseCase)

            viewModel.onSearchQueryChange("Smith")
            assertEquals(1, viewModel.trees.value.size)
            assertEquals("Smith Family", viewModel.trees.value[0].name)

            viewModel.onSearchQueryChange("NonExistent")
            assertEquals(0, viewModel.trees.value.size)
        }

    @Test
    fun `when user is logged in without displayName userName state should be User`() =
        runTest {
            val user = User("123", "test@test.com", null)
            authRepository.setCurrentUser(user)

            viewModel = AuthenticatedHomeViewModel(checkSessionUseCase)

            assertEquals("User", viewModel.userName.value)
        }
}

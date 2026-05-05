package dev.saragones3.genogramia.presentation.splash

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.repository.AuthRepository
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.fakes.FakeAuthRepository
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
class SplashViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when user is logged in, navigates to AuthenticatedHome`() = runTest {
        val fakeRepo = FakeAuthRepository(User("uid", "email", "name"))
        val useCase = CheckSessionUseCase(fakeRepo)
        val viewModel = SplashViewModel(useCase)

        viewModel.uiState.test {
            assertEquals(SplashUiState.NavigateToAuthenticatedHome, awaitItem())
        }
    }

    @Test
    fun `when user is not logged in, navigates to GuestHome`() = runTest {
        val fakeRepo = FakeAuthRepository(null)
        val useCase = CheckSessionUseCase(fakeRepo)
        val viewModel = SplashViewModel(useCase)

        viewModel.uiState.test {
            assertEquals(SplashUiState.NavigateToGuestHome, awaitItem())
        }
    }
}

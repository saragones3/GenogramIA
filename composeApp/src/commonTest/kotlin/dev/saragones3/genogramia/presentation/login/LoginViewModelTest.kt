package dev.saragones3.genogramia.presentation.login

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.AuthError
import dev.saragones3.genogramia.domain.usecase.SignInUseCase
import dev.saragones3.genogramia.fakes.FakeAuthRepository
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
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var signInUseCase: SignInUseCase
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        signInUseCase = SignInUseCase(fakeAuthRepository)
        viewModel = LoginViewModel(signInUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN view model WHEN initialized THEN state is empty`() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals("", state.email)
                assertEquals("", state.password)
                assertFalse(state.isLoading)
                assertNull(state.emailError)
                assertNull(state.passwordError)
                assertNull(state.generalError)
                assertFalse(state.isSuccess)
            }
        }

    @Test
    fun `GIVEN view model WHEN email and password updated THEN state is updated`() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onEvent(LoginEvent.OnDataChanged("test@test.com", "password123"))
                val updatedState = awaitItem()
                assertEquals("test@test.com", updatedState.email)
                assertEquals("password123", updatedState.password)
            }
        }

    @Test
    fun `GIVEN empty credentials WHEN login clicked THEN validation fails`() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onEvent(LoginEvent.OnLoginClicked)
                val errorState = awaitItem()
                assertEquals(LoginUiState.ValidationError.EMPTY, errorState.emailError)
                assertEquals(LoginUiState.ValidationError.EMPTY, errorState.passwordError)
            }
        }

    @Test
    fun `GIVEN invalid email WHEN login clicked THEN validation fails`() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onEvent(LoginEvent.OnDataChanged("invalid-email", "password123"))
                awaitItem()

                viewModel.onEvent(LoginEvent.OnLoginClicked)
                val errorState = awaitItem()
                assertEquals(LoginUiState.ValidationError.INVALID, errorState.emailError)
            }
        }

    @Test
    fun `GIVEN short password WHEN login clicked THEN validation fails`() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onEvent(LoginEvent.OnDataChanged("test@test.com", "short"))
                awaitItem()

                viewModel.onEvent(LoginEvent.OnLoginClicked)
                val errorState = awaitItem()
                assertEquals(LoginUiState.ValidationError.INVALID, errorState.passwordError)
            }
        }

    @Test
    fun `GIVEN valid credentials WHEN login clicked THEN login is successful`() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onEvent(LoginEvent.OnDataChanged("test@test.com", "password123"))
                awaitItem()

                viewModel.onEvent(LoginEvent.OnLoginClicked)

                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)

                val successState = awaitItem()
                assertFalse(successState.isLoading)
                assertTrue(successState.isSuccess)
                assertNull(successState.generalError)
            }
        }

    @Test
    fun `GIVEN invalid credentials WHEN login clicked THEN login fails`() =
        runTest(testDispatcher) {
            fakeAuthRepository.shouldReturnError = true

            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onEvent(LoginEvent.OnDataChanged("test@test.com", "password123"))
                awaitItem()

                viewModel.onEvent(LoginEvent.OnLoginClicked)

                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)

                val errorState = awaitItem()
                assertFalse(errorState.isLoading)
                assertFalse(errorState.isSuccess)
                assertEquals(LoginError.WrongCredentials, errorState.generalError)
            }
        }

    @Test
    fun `GIVEN non-existent user WHEN login clicked THEN user not found error is set`() =
        runTest(testDispatcher) {
            fakeAuthRepository.shouldReturnError = true
            fakeAuthRepository.errorToReturn = AuthError.UserNotFound

            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onEvent(LoginEvent.OnDataChanged("non-existent@test.com", "password123"))
                awaitItem()

                viewModel.onEvent(LoginEvent.OnLoginClicked)

                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)

                val errorState = awaitItem()
                assertFalse(errorState.isLoading)
                assertEquals(LoginError.UserNotFound, errorState.generalError)
            }
        }

    @Test
    fun `GIVEN general error WHEN error shown THEN error is cleared`() =
        runTest(testDispatcher) {
            fakeAuthRepository.shouldReturnError = true
            viewModel.onEvent(LoginEvent.OnDataChanged("test@test.com", "password123"))
            viewModel.onEvent(LoginEvent.OnLoginClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(LoginEvent.OnErrorShown)

            viewModel.uiState.test {
                val state = awaitItem()
                assertNull(state.generalError)
            }
        }

    @Test
    fun `GIVEN login success WHEN success consumed THEN state is reset`() =
        runTest(testDispatcher) {
            viewModel.onEvent(LoginEvent.OnDataChanged("test@test.com", "password123"))
            viewModel.onEvent(LoginEvent.OnLoginClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(LoginEvent.OnLoginSuccessConsumed)

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals("", state.email)
                assertFalse(state.isSuccess)
            }
        }
}

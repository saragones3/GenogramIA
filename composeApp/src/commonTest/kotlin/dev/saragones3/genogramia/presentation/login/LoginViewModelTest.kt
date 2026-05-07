package dev.saragones3.genogramia.presentation.login

import app.cash.turbine.test
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
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeAuthRepository = FakeAuthRepository()
        viewModel = LoginViewModel(fakeAuthRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initial_state_is_empty() =
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
    fun update_email_and_password_changes_state() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onDataChange("test@test.com", "password123")
                val updatedState = awaitItem()
                assertEquals("test@test.com", updatedState.email)
                assertEquals("password123", updatedState.password)
            }
        }

    @Test
    fun login_with_empty_credentials_sets_errors() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.login()
                val errorState = awaitItem()
                assertEquals(LoginUiState.ValidationError.EMPTY, errorState.emailError)
                assertEquals(LoginUiState.ValidationError.EMPTY, errorState.passwordError)
            }
        }

    @Test
    fun login_with_invalid_email_sets_error() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onDataChange("invalid-email", "password123")
                awaitItem()

                viewModel.login()
                val errorState = awaitItem()
                assertEquals(LoginUiState.ValidationError.INVALID, errorState.emailError)
            }
        }

    @Test
    fun login_with_short_password_sets_error() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onDataChange("test@test.com", "short")
                awaitItem()

                viewModel.login()
                val errorState = awaitItem()
                assertEquals(LoginUiState.ValidationError.INVALID, errorState.passwordError)
            }
        }

    @Test
    fun successful_login_updates_state() =
        runTest(testDispatcher) {
            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onDataChange("test@test.com", "password123")
                awaitItem()

                viewModel.login()

                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)

                val successState = awaitItem()
                assertFalse(successState.isLoading)
                assertTrue(successState.isSuccess)
                assertNull(successState.generalError)
            }
        }

    @Test
    fun failed_login_sets_error() =
        runTest(testDispatcher) {
            fakeAuthRepository.shouldReturnError = true

            viewModel.uiState.test {
                awaitItem() // initial

                viewModel.onDataChange("test@test.com", "password123")
                awaitItem()

                viewModel.login()

                val loadingState = awaitItem()
                assertTrue(loadingState.isLoading)

                val errorState = awaitItem()
                assertFalse(errorState.isLoading)
                assertFalse(errorState.isSuccess)
                assertEquals(LoginError.WrongCredentials, errorState.generalError)
            }
        }
}

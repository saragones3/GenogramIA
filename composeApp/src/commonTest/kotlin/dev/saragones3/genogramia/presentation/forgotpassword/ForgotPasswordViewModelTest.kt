package dev.saragones3.genogramia.presentation.forgotpassword

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.AuthError
import dev.saragones3.genogramia.domain.usecase.SendPasswordResetEmailUseCase
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeAuthRepository()
    private val sendPasswordResetEmailUseCase = SendPasswordResetEmailUseCase(repository)
    private lateinit var viewModel: ForgotPasswordViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ForgotPasswordViewModel(sendPasswordResetEmailUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default`() =
        runTest {
            val state = viewModel.state.value
            assertEquals("", state.email)
            assertEquals(false, state.isLoading)
            assertNull(state.emailError)
            assertEquals(false, state.isSuccess)
            assertNull(state.error)
        }

    @Test
    fun `setInitialEmail updates state only if email is empty`() =
        runTest {
            viewModel.setInitialEmail("initial@test.com")
            assertEquals("initial@test.com", viewModel.state.value.email)

            viewModel.setInitialEmail("other@test.com")
            assertEquals("initial@test.com", viewModel.state.value.email)
        }

    @Test
    fun `when email changes state is updated`() =
        runTest {
            viewModel.onEmailChange("test@example.com")
            assertEquals("test@example.com", viewModel.state.value.email)
        }

    @Test
    fun `when email is empty validation fails`() =
        runTest {
            viewModel.onEmailChange("")
            viewModel.sendResetEmail()
            assertEquals(ForgotPasswordState.ValidationError.EMPTY, viewModel.state.value.emailError)
        }

    @Test
    fun `when email is invalid validation fails`() =
        runTest {
            viewModel.onEmailChange("invalid-email")
            viewModel.sendResetEmail()
            assertEquals(ForgotPasswordState.ValidationError.INVALID, viewModel.state.value.emailError)
        }

    @Test
    fun `when send is successful success state is true`() =
        runTest {
            viewModel.onEmailChange("test@example.com")

            viewModel.state.test {
                assertEquals("test@example.com", awaitItem().email)

                viewModel.sendResetEmail()

                assertEquals(true, awaitItem().isLoading)

                val successState = awaitItem()
                assertEquals(false, successState.isLoading)
                assertTrue(successState.isSuccess)
            }
        }

    @Test
    fun `when user not found error is updated`() =
        runTest {
            repository.shouldReturnError = true
            repository.errorToReturn = AuthError.UserNotFound

            viewModel.onEmailChange("test@example.com")
            viewModel.sendResetEmail()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(ForgotPasswordState.ForgotPasswordError.UserNotFound, state.error)
            assertEquals(false, state.isLoading)
        }

    @Test
    fun `when generic error occurs error is updated`() =
        runTest {
            repository.shouldReturnError = true
            repository.errorToReturn = Exception("Generic error")

            viewModel.onEmailChange("test@example.com")
            viewModel.sendResetEmail()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(ForgotPasswordState.ForgotPasswordError.Generic, state.error)
        }

    @Test
    fun `successConsumed resets success state`() =
        runTest {
            viewModel.onEmailChange("test@example.com")
            viewModel.sendResetEmail()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.state.value.isSuccess)
            viewModel.successConsumed()
            assertEquals(false, viewModel.state.value.isSuccess)
        }

    @Test
    fun `errorShown resets error state`() =
        runTest {
            repository.shouldReturnError = true
            viewModel.onEmailChange("test@example.com")
            viewModel.sendResetEmail()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.state.value.error != null)
            viewModel.errorShown()
            assertNull(viewModel.state.value.error)
        }
}

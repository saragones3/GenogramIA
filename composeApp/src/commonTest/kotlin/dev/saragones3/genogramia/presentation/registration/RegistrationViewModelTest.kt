package dev.saragones3.genogramia.presentation.registration

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.AuthError
import dev.saragones3.genogramia.domain.usecase.SignUpUseCase
import dev.saragones3.genogramia.fakes.FakeAuthRepository
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.error_email_already_in_use
import genogramia.composeapp.generated.resources.error_invalid_email
import genogramia.composeapp.generated.resources.error_invalid_password
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
class RegistrationViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeAuthRepository()
    private val signUpUseCase = SignUpUseCase(repository)
    private lateinit var viewModel: RegistrationViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegistrationViewModel(signUpUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN view model WHEN initialized THEN state is empty`() =
        runTest {
            val state = viewModel.state.value
            assertEquals("", state.name)
            assertEquals("", state.email)
            assertEquals("", state.password)
            assertNull(state.nameError)
            assertNull(state.emailError)
            assertNull(state.passwordError)
        }

    @Test
    fun `GIVEN empty fields WHEN sign up clicked THEN validation fails`() =
        runTest {
            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)

            val state = viewModel.state.value
            assertEquals(RegistrationState.ValidationError.EMPTY, state.nameError)
            assertEquals(RegistrationState.ValidationError.EMPTY, state.emailError)
            assertEquals(RegistrationState.ValidationError.EMPTY, state.passwordError)
        }

    @Test
    fun `GIVEN invalid email format WHEN sign up clicked THEN validation fails`() =
        runTest {
            val invalidEmails =
                listOf(
                    "invalid-email",
                    "user@",
                    "@domain.com",
                    "user@domain",
                    "user name@domain.com",
                )

            invalidEmails.forEach { email ->
                viewModel.onEvent(RegistrationEvent.OnDataChanged("Test", email, "password123"))
                viewModel.onEvent(RegistrationEvent.OnSignUpClicked)

                val state = viewModel.state.value
                assertEquals(
                    RegistrationState.ValidationError.INVALID,
                    state.emailError,
                    "Email '$email' should be considered invalid",
                )
            }
        }

    @Test
    fun `GIVEN invalid email error WHEN email corrected THEN error is cleared`() =
        runTest {
            // Given an invalid email state
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test", "invalid-email", "password123"))
            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)
            assertEquals(RegistrationState.ValidationError.INVALID, viewModel.state.value.emailError)

            // When the email is corrected
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test", "valid@email.com", "password123"))

            // Then the error is cleared
            assertNull(viewModel.state.value.emailError)
        }

    @Test
    fun `GIVEN invalid email WHEN sign up clicked THEN name and password errors are not set`() =
        runTest {
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Valid Name", "invalid-email", "validPassword123"))

            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)

            val state = viewModel.state.value
            assertEquals(RegistrationState.ValidationError.INVALID, state.emailError)
            assertNull(state.nameError)
            assertNull(state.passwordError)
        }

    @Test
    fun `GIVEN valid data WHEN sign up clicked THEN registration is successful`() =
        runTest {
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test User", "test@example.com", "password123"))

            viewModel.state.test {
                viewModel.onEvent(RegistrationEvent.OnSignUpClicked)

                // Skip initial state and field changes if they were emitted
                var lastState = awaitItem()
                while (lastState.name != "Test User" ||
                    lastState.email != "test@example.com" ||
                    lastState.password != "password123"
                ) {
                    lastState = awaitItem()
                }

                // First state after click: loading
                assertEquals(true, awaitItem().isLoading)

                // Final state: success
                val finalState = awaitItem()
                assertEquals(false, finalState.isLoading)
                assertTrue(finalState.isRegistrationSuccess)
            }
        }

    @Test
    fun `GIVEN email in use WHEN sign up clicked THEN email in use error is shown`() =
        runTest {
            repository.shouldReturnError = true
            repository.errorToReturn = AuthError.EmailAlreadyInUse
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test User", "test@example.com", "password123"))

            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(Res.string.error_email_already_in_use, state.generalError)
            assertEquals(false, state.isLoading)
        }

    @Test
    fun `GIVEN invalid email response WHEN sign up clicked THEN invalid email error is shown`() =
        runTest {
            repository.shouldReturnError = true
            repository.errorToReturn = AuthError.InvalidEmail
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test User", "test@example.com", "password123"))

            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(Res.string.error_invalid_email, state.generalError)
        }

    @Test
    fun `GIVEN weak password response WHEN sign up clicked THEN weak password error is shown`() =
        runTest {
            repository.shouldReturnError = true
            repository.errorToReturn = AuthError.WeakPassword
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test User", "test@example.com", "password123"))

            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(Res.string.error_invalid_password, state.generalError)
        }

    @Test
    fun `GIVEN registration success WHEN success consumed THEN state is reset`() =
        runTest {
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test User", "test@example.com", "password123"))

            viewModel.onEvent(RegistrationEvent.OnRegistrationSuccessConsumed)

            val state = viewModel.state.value
            assertEquals("", state.name)
            assertEquals("", state.email)
            assertFalse(state.isRegistrationSuccess)
        }

    @Test
    fun `GIVEN short password WHEN sign up clicked THEN validation fails`() =
        runTest {
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test", "test@example.com", "short"))
            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)

            val state = viewModel.state.value
            assertEquals(RegistrationState.ValidationError.INVALID, state.passwordError)
        }

    @Test
    fun `GIVEN short password error WHEN password corrected THEN error is cleared`() =
        runTest {
            // Given an invalid password state
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test", "test@example.com", "short"))
            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)
            assertEquals(RegistrationState.ValidationError.INVALID, viewModel.state.value.passwordError)

            // When the password is corrected
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test", "test@example.com", "password123"))

            // Then the error is cleared
            assertNull(viewModel.state.value.passwordError)
        }

    @Test
    fun `GIVEN empty name WHEN sign up clicked THEN validation fails`() =
        runTest {
            viewModel.onEvent(RegistrationEvent.OnDataChanged("", "test@example.com", "password123"))
            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)

            val state = viewModel.state.value
            assertEquals(RegistrationState.ValidationError.EMPTY, state.nameError)
        }

    @Test
    fun `GIVEN empty name error WHEN name corrected THEN error is cleared`() =
        runTest {
            // Given an empty name state
            viewModel.onEvent(RegistrationEvent.OnDataChanged("", "test@example.com", "password123"))
            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)
            assertEquals(RegistrationState.ValidationError.EMPTY, viewModel.state.value.nameError)

            // When the name is corrected
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Test", "test@example.com", "password123"))

            // Then the error is cleared
            assertNull(viewModel.state.value.nameError)
        }
}

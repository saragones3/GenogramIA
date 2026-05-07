package dev.saragones3.genogramia.presentation.registration

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.usecase.SignUpUseCase
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
    fun `initial state is empty`() =
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
    fun `when fields are empty validation fails`() =
        runTest {
            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)

            val state = viewModel.state.value
            assertEquals(RegistrationState.ValidationError.EMPTY, state.nameError)
            assertEquals(RegistrationState.ValidationError.EMPTY, state.emailError)
            assertEquals(RegistrationState.ValidationError.EMPTY, state.passwordError)
        }

    @Test
    fun `when email format is invalid validation fails`() =
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
    fun `when email is corrected after invalid format, email error is cleared`() =
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
    fun `when email is invalid, name and password errors are not affected`() =
        runTest {
            viewModel.onEvent(RegistrationEvent.OnDataChanged("Valid Name", "invalid-email", "validPassword123"))

            viewModel.onEvent(RegistrationEvent.OnSignUpClicked)

            val state = viewModel.state.value
            assertEquals(RegistrationState.ValidationError.INVALID, state.emailError)
            assertNull(state.nameError)
            assertNull(state.passwordError)
        }

    @Test
    fun `when registration is successful success state is true`() =
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
}

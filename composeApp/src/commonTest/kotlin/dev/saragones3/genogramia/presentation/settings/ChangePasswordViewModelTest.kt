package dev.saragones3.genogramia.presentation.settings

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.usecase.UpdatePasswordUseCase
import dev.saragones3.genogramia.fakes.FakeAuthRepository
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.error_unknown
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
class ChangePasswordViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeAuthRepository()
    private val updatePasswordUseCase = UpdatePasswordUseCase(repository)
    private lateinit var viewModel: ChangePasswordViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChangePasswordViewModel(updatePasswordUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() =
        runTest {
            val state = viewModel.state.value
            assertEquals("", state.currentPassword)
            assertEquals("", state.newPassword)
            assertEquals("", state.confirmPassword)
            assertNull(state.currentPasswordError)
            assertNull(state.passwordError)
            assertNull(state.confirmError)
        }

    @Test
    fun `when fields are empty validation fails`() =
        runTest {
            viewModel.savePassword()

            val state = viewModel.state.value
            assertEquals(ChangePasswordState.ValidationError.EMPTY, state.currentPasswordError)
            assertEquals(ChangePasswordState.ValidationError.EMPTY, state.passwordError)
            assertEquals(ChangePasswordState.ValidationError.EMPTY, state.confirmError)
        }

    @Test
    fun `when passwords do not match validation fails`() =
        runTest {
            viewModel.onDataChange("oldPass", "password123", "password321")
            viewModel.savePassword()

            val state = viewModel.state.value
            assertNull(state.passwordError)
            assertEquals(ChangePasswordState.ValidationError.MISMATCH, state.confirmError)
        }

    @Test
    fun `when password is too short validation fails`() =
        runTest {
            viewModel.onDataChange("oldPass", "short", "short")
            viewModel.savePassword()

            val state = viewModel.state.value
            assertEquals(ChangePasswordState.ValidationError.TOO_SHORT, state.passwordError)
            assertNull(state.confirmError)
        }

    @Test
    fun `when update is successful success state is true`() =
        runTest {
            viewModel.onDataChange("oldPass", "newPassword123", "newPassword123")

            viewModel.state.test {
                viewModel.savePassword()

                // Skip initial state and data change state
                var lastState = awaitItem()
                while (lastState.newPassword != "newPassword123") {
                    lastState = awaitItem()
                }

                // Loading state
                assertEquals(true, awaitItem().isLoading)

                // Success state
                val finalState = awaitItem()
                assertEquals(false, finalState.isLoading)
                assertTrue(finalState.isSuccess)
            }
        }

    @Test
    fun `when update fails general error is updated`() =
        runTest {
            repository.shouldReturnError = true
            viewModel.onDataChange("oldPass", "newPassword123", "newPassword123")

            viewModel.savePassword()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(Res.string.error_unknown, state.generalError)
            assertEquals(false, state.isLoading)
        }

    @Test
    fun `successConsumed resets success state`() =
        runTest {
            viewModel.onDataChange("oldPass", "newPassword123", "newPassword123")
            viewModel.savePassword()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.state.value.isSuccess)

            viewModel.successConsumed()

            assertEquals(false, viewModel.state.value.isSuccess)
        }
}

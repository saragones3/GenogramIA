package dev.saragones3.genogramia.presentation.settings

import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.DeleteAccountUseCase
import dev.saragones3.genogramia.domain.usecase.SignOutUseCase
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
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeAuthRepository()
    private val checkSessionUseCase = CheckSessionUseCase(repository)
    private val signOutUseCase = SignOutUseCase(repository)
    private val deleteAccountUseCase = DeleteAccountUseCase(repository)
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN logged in user WHEN view model initialized THEN state has user info`() =
        runTest {
            val user = User("1", "test@example.com", "Test User")
            repository.setCurrentUser(user)

            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            assertEquals(user, viewModel.state.value.user)
        }

    @Test
    fun `GIVEN no user WHEN view model initialized THEN user state is null`() =
        runTest {
            repository.setCurrentUser(null)

            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            assertNull(viewModel.state.value.user)
        }

    @Test
    fun `GIVEN view model WHEN logout clicked THEN logout confirmation is shown`() =
        runTest {
            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            viewModel.onEvent(SettingsEvent.OnLogOutClicked)

            assertTrue(viewModel.state.value.showLogoutConfirmation)
        }

    @Test
    fun `GIVEN view model WHEN delete account clicked THEN delete confirmation is shown`() =
        runTest {
            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            viewModel.onEvent(SettingsEvent.OnDeleteAccountClicked)

            assertTrue(viewModel.state.value.showDeleteConfirmation)
        }

    @Test
    fun `GIVEN logged in user WHEN logout confirmed THEN user is signed out`() =
        runTest {
            val user = User("1", "test@example.com", "Test User")
            repository.setCurrentUser(user)
            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            viewModel.onEvent(SettingsEvent.OnLogoutConfirmed)
            testDispatcher.scheduler.advanceUntilIdle()

            assertNull(repository.getCurrentUser())
            assertTrue(viewModel.state.value.isLoggedOut)
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `GIVEN logged in user WHEN delete confirmed THEN user is deleted`() =
        runTest {
            val user = User("1", "test@example.com", "Test User")
            repository.setCurrentUser(user)
            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            viewModel.onEvent(SettingsEvent.OnDeleteConfirmed)
            testDispatcher.scheduler.advanceUntilIdle()

            assertNull(repository.getCurrentUser())
            assertTrue(viewModel.state.value.isLoggedOut)
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `GIVEN dialogs shown WHEN dismissed THEN confirmation states are false`() =
        runTest {
            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            viewModel.onEvent(SettingsEvent.OnLogOutClicked)
            assertTrue(viewModel.state.value.showLogoutConfirmation)

            viewModel.onEvent(SettingsEvent.OnDismissDialogs)
            assertFalse(viewModel.state.value.showLogoutConfirmation)
            assertFalse(viewModel.state.value.showDeleteConfirmation)
        }
}

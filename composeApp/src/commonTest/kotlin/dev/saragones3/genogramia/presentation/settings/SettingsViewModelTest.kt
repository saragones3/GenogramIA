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
    fun `when user is logged in state is updated with user info`() =
        runTest {
            val user = User("1", "test@example.com", "Test User")
            repository.setCurrentUser(user)

            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            assertEquals(user, viewModel.state.value.user)
        }

    @Test
    fun `when user is not logged in user state is null`() =
        runTest {
            repository.setCurrentUser(null)

            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            assertNull(viewModel.state.value.user)
        }

    @Test
    fun `when logout is clicked logout confirmation is shown`() =
        runTest {
            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            viewModel.onEvent(SettingsEvent.OnLogOutClicked)

            assertTrue(viewModel.state.value.showLogoutConfirmation)
        }

    @Test
    fun `when delete account is clicked delete confirmation is shown`() =
        runTest {
            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            viewModel.onEvent(SettingsEvent.OnDeleteAccountClicked)

            assertTrue(viewModel.state.value.showDeleteConfirmation)
        }

    @Test
    fun `when logout is confirmed repository is updated and logout state is true`() =
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
    fun `when delete is confirmed repository is updated and logout state is true`() =
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
    fun `when dialogs are dismissed confirmation states are false`() =
        runTest {
            viewModel = SettingsViewModel(checkSessionUseCase, signOutUseCase, deleteAccountUseCase)

            viewModel.onEvent(SettingsEvent.OnLogOutClicked)
            assertTrue(viewModel.state.value.showLogoutConfirmation)

            viewModel.onEvent(SettingsEvent.OnDismissDialogs)
            assertFalse(viewModel.state.value.showLogoutConfirmation)
            assertFalse(viewModel.state.value.showDeleteConfirmation)
        }
}

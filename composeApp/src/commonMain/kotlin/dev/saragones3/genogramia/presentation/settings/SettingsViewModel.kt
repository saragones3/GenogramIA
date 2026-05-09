package dev.saragones3.genogramia.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.DeleteAccountUseCase
import dev.saragones3.genogramia.domain.usecase.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val user = checkSessionUseCase()
        _state.update { it.copy(user = user) }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.OnLogOutClicked -> {
                _state.update { it.copy(showLogoutConfirmation = true) }
            }

            SettingsEvent.OnDeleteAccountClicked -> {
                _state.update { it.copy(showDeleteConfirmation = true) }
            }

            SettingsEvent.OnLogoutConfirmed -> {
                _state.update { it.copy(showLogoutConfirmation = false, isLoading = true) }
                viewModelScope.launch {
                    signOutUseCase()
                    _state.update { it.copy(isLoading = false, isLoggedOut = true) }
                }
            }

            SettingsEvent.OnDeleteConfirmed -> {
                _state.update { it.copy(showDeleteConfirmation = false, isLoading = true) }
                viewModelScope.launch {
                    deleteAccountUseCase()
                    _state.update { it.copy(isLoading = false, isLoggedOut = true) }
                }
            }

            SettingsEvent.OnDismissDialogs -> {
                _state.update { it.copy(showLogoutConfirmation = false, showDeleteConfirmation = false) }
            }
        }
    }
}

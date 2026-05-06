package dev.saragones3.genogramia.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onDataChange(
        email: String,
        password: String,
    ) {
        _uiState.update { it.copy(email = email, password = password, error = null) }
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = LoginError.EmptyValues) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.signInWithEmailAndPassword(email, password)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, error = LoginError.WrongCredentials) }
            }
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }
}

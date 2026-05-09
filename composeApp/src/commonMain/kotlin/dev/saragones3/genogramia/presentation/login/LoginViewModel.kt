package dev.saragones3.genogramia.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.AuthError
import dev.saragones3.genogramia.domain.usecase.SignInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onDataChange(
        email: String,
        password: String,
    ) {
        _uiState.update {
            it.copy(
                email = email,
                password = password,
                emailError = if (it.email != email) null else it.emailError,
                passwordError = if (it.password != password) null else it.passwordError,
                generalError = null,
            )
        }
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (!validateFields(email, password)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            val result = signInUseCase(email, password)

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }.onFailure { error ->
                    val loginError =
                        when (error) {
                            is AuthError.UserNotFound -> LoginError.UserNotFound
                            else -> LoginError.WrongCredentials
                        }
                    _uiState.update { it.copy(isLoading = false, generalError = loginError) }
                }
        }
    }

    private fun validateFields(
        email: String,
        password: String,
    ): Boolean {
        var emailError: LoginUiState.ValidationError? = null
        var passwordError: LoginUiState.ValidationError? = null

        if (email.isBlank()) {
            emailError = LoginUiState.ValidationError.EMPTY
        } else if (!isValidEmail(email)) {
            emailError = LoginUiState.ValidationError.INVALID
        }

        if (password.isEmpty()) {
            passwordError = LoginUiState.ValidationError.EMPTY
        } else if (password.length < 8) {
            passwordError = LoginUiState.ValidationError.INVALID
        }

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
            )
        }

        return emailError == null && passwordError == null
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
        return emailRegex.matches(email)
    }

    fun loginSuccessConsumed() {
        _uiState.value = LoginUiState()
    }

    fun errorShown() {
        _uiState.update { it.copy(generalError = null) }
    }
}

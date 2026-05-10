package dev.saragones3.genogramia.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.AuthError
import dev.saragones3.genogramia.domain.usecase.SendPasswordResetEmailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    fun setInitialEmail(email: String?) {
        if (email != null && _state.value.email.isEmpty()) {
            _state.update { it.copy(email = email) }
        }
    }

    fun onEmailChange(email: String) {
        _state.update {
            it.copy(
                email = email,
                emailError = if (it.email != email) null else it.emailError,
                error = null,
            )
        }
    }

    fun sendResetEmail() {
        val email = _state.value.email.trim()

        if (!validateEmail(email)) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = sendPasswordResetEmailUseCase(email)

            result
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }.onFailure { error ->
                    val forgotPasswordError =
                        when (error) {
                            is AuthError.UserNotFound -> ForgotPasswordState.ForgotPasswordError.UserNotFound
                            else -> ForgotPasswordState.ForgotPasswordError.Generic
                        }
                    _state.update { it.copy(isLoading = false, error = forgotPasswordError) }
                }
        }
    }

    private fun validateEmail(email: String): Boolean {
        val error =
            when {
                email.isBlank() -> ForgotPasswordState.ValidationError.EMPTY
                !isValidEmail(email) -> ForgotPasswordState.ValidationError.INVALID
                else -> null
            }

        _state.update { it.copy(emailError = error) }
        return error == null
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
        return emailRegex.matches(email)
    }

    fun successConsumed() {
        _state.update { it.copy(isSuccess = false) }
    }

    fun errorShown() {
        _state.update { it.copy(error = null) }
    }
}

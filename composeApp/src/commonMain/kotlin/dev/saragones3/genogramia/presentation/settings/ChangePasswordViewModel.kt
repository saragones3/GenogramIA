package dev.saragones3.genogramia.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.AuthError
import dev.saragones3.genogramia.domain.usecase.UpdatePasswordUseCase
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.error_invalid_credentials
import genogramia.composeapp.generated.resources.error_unknown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val updatePasswordUseCase: UpdatePasswordUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ChangePasswordState())
    val state: StateFlow<ChangePasswordState> = _state.asStateFlow()

    fun onDataChange(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
    ) {
        _state.update {
            it.copy(
                currentPassword = currentPassword,
                newPassword = newPassword,
                confirmPassword = confirmPassword,
                currentPasswordError = null,
                passwordError = null,
                confirmError = null,
                generalError = null,
            )
        }
    }

    fun savePassword() {
        val current = _state.value

        val currentPasswordError =
            when {
                current.currentPassword.isBlank() -> ChangePasswordState.ValidationError.EMPTY
                else -> null
            }

        val passwordError =
            when {
                current.newPassword.isBlank() -> ChangePasswordState.ValidationError.EMPTY
                current.newPassword.length < 8 -> ChangePasswordState.ValidationError.TOO_SHORT
                else -> null
            }

        val confirmError =
            when {
                current.confirmPassword.isBlank() -> ChangePasswordState.ValidationError.EMPTY
                current.newPassword != current.confirmPassword -> ChangePasswordState.ValidationError.MISMATCH
                else -> null
            }

        if (currentPasswordError != null || passwordError != null || confirmError != null) {
            _state.update {
                it.copy(
                    currentPasswordError = currentPasswordError,
                    passwordError = passwordError,
                    confirmError = confirmError,
                )
            }
            return
        }

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                updatePasswordUseCase(current.currentPassword, current.newPassword)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                val errorRes =
                    when (e) {
                        is AuthError.WrongPassword -> Res.string.error_invalid_credentials
                        else -> Res.string.error_unknown
                    }
                _state.update { it.copy(isLoading = false, generalError = errorRes) }
            }
        }
    }

    fun successConsumed() {
        _state.update { it.copy(isSuccess = false) }
    }
}

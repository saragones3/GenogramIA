package dev.saragones3.genogramia.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.usecase.UpdatePasswordUseCase
import genogramia.composeapp.generated.resources.Res
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
        newPassword: String,
        confirmPassword: String,
    ) {
        _state.update {
            it.copy(
                newPassword = newPassword,
                confirmPassword = confirmPassword,
                passwordError = null,
                confirmError = null,
                generalError = null,
            )
        }
    }

    fun savePassword() {
        val current = _state.value
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

        if (passwordError != null || confirmError != null) {
            _state.update { it.copy(passwordError = passwordError, confirmError = confirmError) }
            return
        }

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                updatePasswordUseCase(current.newPassword)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, generalError = Res.string.error_unknown) }
            }
        }
    }

    fun successConsumed() {
        _state.update { it.copy(isSuccess = false) }
    }
}

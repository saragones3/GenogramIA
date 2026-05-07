package dev.saragones3.genogramia.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.usecase.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val signUpUseCase: SignUpUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(RegistrationState())
    val state: StateFlow<RegistrationState> = _state.asStateFlow()

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            is RegistrationEvent.OnDataChanged -> {
                _state.update {
                    it.copy(
                        name = event.name,
                        email = event.email,
                        password = event.password,
                        nameError = if (it.name != event.name) null else it.nameError,
                        emailError = if (it.email != event.email) null else it.emailError,
                        passwordError = if (it.password != event.password) null else it.passwordError,
                    )
                }
            }

            is RegistrationEvent.OnSignUpClicked -> {
                signUp()
            }
        }
    }

    private fun signUp() {
        if (!validateFields()) return

        _state.update { it.copy(isLoading = true, generalError = null) }

        viewModelScope.launch {
            val result =
                signUpUseCase(
                    name = _state.value.name,
                    email = _state.value.email,
                    password = _state.value.password,
                )

            result
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isRegistrationSuccess = true) }
                }.onFailure { error ->
                    _state.update { it.copy(isLoading = false, generalError = error.message) }
                }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        val name = _state.value.name
        val email = _state.value.email
        val password = _state.value.password

        if (name.isBlank()) {
            _state.update { it.copy(nameError = RegistrationState.ValidationError.EMPTY) }
            isValid = false
        }

        if (email.isBlank()) {
            _state.update { it.copy(emailError = RegistrationState.ValidationError.EMPTY) }
            isValid = false
        } else if (!isValidEmail(email)) {
            _state.update { it.copy(emailError = RegistrationState.ValidationError.INVALID) }
            isValid = false
        }

        if (password.isEmpty()) {
            _state.update { it.copy(passwordError = RegistrationState.ValidationError.EMPTY) }
            isValid = false
        } else if (password.length < 8) {
            _state.update { it.copy(passwordError = RegistrationState.ValidationError.INVALID) }
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
        return emailRegex.matches(email)
    }
}

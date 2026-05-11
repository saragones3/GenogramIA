package dev.saragones3.genogramia.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.AuthError
import dev.saragones3.genogramia.domain.usecase.SignUpUseCase
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.error_email_already_in_use
import genogramia.composeapp.generated.resources.error_invalid_email
import genogramia.composeapp.generated.resources.error_invalid_password
import genogramia.composeapp.generated.resources.error_unknown
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

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
                        generalError = null,
                    )
                }
            }

            is RegistrationEvent.OnSignUpClicked -> {
                signUp()
            }

            RegistrationEvent.OnGoogleSignUpClicked -> {
                // TODO: Implement Google Sign Up
            }

            RegistrationEvent.OnAppleSignUpClicked -> {
                // TODO: Implement Apple Sign Up
            }

            RegistrationEvent.OnRegistrationSuccessConsumed -> {
                registrationSuccessConsumed()
            }
        }
    }

    private fun registrationSuccessConsumed() {
        _state.value = RegistrationState()
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
                    val authError = error as? AuthError ?: AuthError.Unknown
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = authError.toErrorMessage(),
                        )
                    }
                }
        }
    }

    private fun AuthError.toErrorMessage(): StringResource =
        when (this) {
            AuthError.EmailAlreadyInUse -> Res.string.error_email_already_in_use
            AuthError.InvalidEmail -> Res.string.error_invalid_email
            AuthError.WeakPassword -> Res.string.error_invalid_password
            else -> Res.string.error_unknown
        }

    private fun validateFields(): Boolean {
        val name = _state.value.name
        val email = _state.value.email
        val password = _state.value.password

        var nameError: RegistrationState.ValidationError? = null
        var emailError: RegistrationState.ValidationError? = null
        var passwordError: RegistrationState.ValidationError? = null

        if (name.isBlank()) {
            nameError = RegistrationState.ValidationError.EMPTY
        }

        if (email.isBlank()) {
            emailError = RegistrationState.ValidationError.EMPTY
        } else if (!isValidEmail(email)) {
            emailError = RegistrationState.ValidationError.INVALID
        }

        if (password.isEmpty()) {
            passwordError = RegistrationState.ValidationError.EMPTY
        } else if (password.length < 8) {
            passwordError = RegistrationState.ValidationError.INVALID
        }

        _state.update {
            it.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
            )
        }

        return nameError == null && emailError == null && passwordError == null
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
        return emailRegex.matches(email)
    }
}

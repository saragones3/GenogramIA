package dev.saragones3.genogramia.presentation.addperson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.AddPersonUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPersonViewModel(
    private val addPersonUseCase: AddPersonUseCase,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private val _state = MutableStateFlow(AddPersonState())
    val state: StateFlow<AddPersonState> = _state.asStateFlow()

    fun onEvent(event: AddPersonEvent) {
        when (event) {
            is AddPersonEvent.OnFirstNameChanged -> {
                _state.update { it.copy(person = it.person.copy(firstName = event.firstName), firstNameError = null) }
            }

            is AddPersonEvent.OnLastNameChanged -> {
                _state.update { it.copy(person = it.person.copy(lastName = event.lastName), lastNameError = null) }
            }

            is AddPersonEvent.OnBiologicalSexChanged -> {
                _state.update { it.copy(person = it.person.copy(biologicalSex = event.sex), biologicalSexError = null) }
            }

            is AddPersonEvent.OnSexualOrientationChanged -> {
                _state.update {
                    it.copy(
                        person = it.person.copy(sexualOrientation = event.orientation),
                        sexualOrientationError = null,
                    )
                }
            }

            is AddPersonEvent.OnBirthDateSelected -> {
                val formattedDate = dateFormatter.formatDate(event.millis, event.pattern)
                _state.update {
                    it.copy(
                        person = it.person.copy(birthDate = formattedDate),
                        birthDateMillis = event.millis,
                        birthDateError = null,
                    )
                }
            }

            is AddPersonEvent.OnDeathDateSelected -> {
                val formattedDate = dateFormatter.formatDate(event.millis, event.pattern)
                _state.update {
                    it.copy(
                        person = it.person.copy(deathDate = formattedDate),
                        deathDateMillis = event.millis,
                    )
                }
            }

            is AddPersonEvent.OnShowBirthDatePicker -> {
                _state.update { it.copy(showBirthDatePicker = event.show) }
            }

            is AddPersonEvent.OnShowDeathDatePicker -> {
                _state.update { it.copy(showDeathDatePicker = event.show) }
            }

            is AddPersonEvent.OnSaveClicked -> {
                savePerson(event.treeId)
            }

            AddPersonEvent.OnResetState -> {
                _state.update { AddPersonState() }
            }
        }
    }

    private fun savePerson(treeId: String) {
        val person = _state.value.person

        var firstNameError: AddPersonState.ValidationError? = null
        var lastNameError: AddPersonState.ValidationError? = null
        var birthDateError: AddPersonState.ValidationError? = null
        var biologicalSexError: AddPersonState.ValidationError? = null
        var sexualOrientationError: AddPersonState.ValidationError? = null

        var isValid = true

        if (person.firstName.isBlank()) {
            firstNameError = AddPersonState.ValidationError.EMPTY
            isValid = false
        }
        if (person.lastName.isBlank()) {
            lastNameError = AddPersonState.ValidationError.EMPTY
            isValid = false
        }
        if (person.birthDate.isNullOrBlank()) {
            birthDateError = AddPersonState.ValidationError.EMPTY
            isValid = false
        }
        if (person.biologicalSex == Person.BiologicalSex.UNKNOWN) {
            biologicalSexError = AddPersonState.ValidationError.EMPTY
            isValid = false
        }
        if (person.sexualOrientation == Person.SexualOrientation.UNKNOWN) {
            sexualOrientationError = AddPersonState.ValidationError.EMPTY
            isValid = false
        }

        if (!isValid) {
            _state.update {
                it.copy(
                    firstNameError = firstNameError,
                    lastNameError = lastNameError,
                    birthDateError = birthDateError,
                    biologicalSexError = biologicalSexError,
                    sexualOrientationError = sexualOrientationError,
                )
            }
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            addPersonUseCase(treeId, person)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }.onFailure {
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }
}

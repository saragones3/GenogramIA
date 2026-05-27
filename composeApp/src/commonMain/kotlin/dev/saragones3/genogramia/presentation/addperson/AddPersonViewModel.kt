package dev.saragones3.genogramia.presentation.addperson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.AddPersonUseCase
import dev.saragones3.genogramia.domain.usecase.GetPersonUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePersonUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddPersonViewModel(
    private val addPersonUseCase: AddPersonUseCase,
    private val updatePersonUseCase: UpdatePersonUseCase,
    private val getPersonUseCase: GetPersonUseCase,
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
                        person =
                            it.person.copy(
                                birthDateMillis = event.millis,
                                birthDateText = formattedDate,
                            ),
                        birthDateError = null,
                    )
                }
            }

            is AddPersonEvent.OnDeathDateSelected -> {
                val formattedDate = dateFormatter.formatDate(event.millis, event.pattern)
                _state.update {
                    it.copy(
                        person =
                            it.person.copy(
                                deathDateMillis = event.millis,
                                deathDateText = formattedDate,
                            ),
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

            is AddPersonEvent.Initialize -> {
                initialize(event.treeId, event.personId, event.x, event.y)
            }

            AddPersonEvent.OnResetState -> {
                _state.update { AddPersonState() }
            }
        }
    }

    private fun initialize(
        treeId: String,
        personId: String?,
        x: Float? = null,
        y: Float? = null,
    ) {
        if (personId == null) {
            _state.update {
                AddPersonState(
                    person =
                        AddPersonUi(
                            x = x ?: 0f,
                            y = y ?: 0f,
                        ),
                )
            }
            return
        }

        _state.update { it.copy(isLoading = true, personId = personId) }
        viewModelScope.launch {
            val person = getPersonUseCase(treeId, personId)
            if (person != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        person =
                            it.person.copy(
                                firstName = person.firstName,
                                lastName = person.lastName,
                                biologicalSex = person.biologicalSex,
                                sexualOrientation = person.sexualOrientation,
                                birthDateMillis = person.birthDate,
                                birthDateText =
                                    dateFormatter.formatDate(person.birthDate, "dd/MM/yyyy"),
                                deathDateMillis = person.deathDate,
                                deathDateText =
                                    person.deathDate?.let { date ->
                                        dateFormatter.formatDate(date, "dd/MM/yyyy")
                                    } ?: "",
                                x = person.x,
                                y = person.y,
                            ),
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun savePerson(treeId: String) {
        val personUi = _state.value.person

        var firstNameError: AddPersonState.ValidationError? = null
        var lastNameError: AddPersonState.ValidationError? = null
        var birthDateError: AddPersonState.ValidationError? = null
        var biologicalSexError: AddPersonState.ValidationError? = null
        var sexualOrientationError: AddPersonState.ValidationError? = null

        var isValid = true

        if (personUi.firstName.isBlank()) {
            firstNameError = AddPersonState.ValidationError.EMPTY
            isValid = false
        }
        if (personUi.lastName.isBlank()) {
            lastNameError = AddPersonState.ValidationError.EMPTY
            isValid = false
        }
        if (personUi.birthDateMillis == null) {
            birthDateError = AddPersonState.ValidationError.EMPTY
            isValid = false
        }
        if (personUi.biologicalSex == Person.BiologicalSex.UNKNOWN) {
            biologicalSexError = AddPersonState.ValidationError.EMPTY
            isValid = false
        }
        if (personUi.sexualOrientation == Person.SexualOrientation.UNKNOWN) {
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

        val person =
            Person(
                id = _state.value.personId ?: "",
                firstName = personUi.firstName,
                lastName = personUi.lastName,
                birthDate = personUi.birthDateMillis ?: 0L,
                biologicalSex = personUi.biologicalSex,
                sexualOrientation = personUi.sexualOrientation,
                deathDate = personUi.deathDateMillis,
                x = personUi.x,
                y = personUi.y,
            )

        viewModelScope.launch {
            val result =
                if (_state.value.personId != null) {
                    updatePersonUseCase(treeId, person)
                } else {
                    addPersonUseCase(treeId, person)
                }

            result
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }.onFailure {
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }
}

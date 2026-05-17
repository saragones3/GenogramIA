package dev.saragones3.genogramia.presentation.newtree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.NewTreeUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewTreeViewModel(
    private val newTreeUseCase: NewTreeUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private val _state = MutableStateFlow(NewTreeState())
    val state: StateFlow<NewTreeState> = _state.asStateFlow()

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        val user = checkSessionUseCase()
        _state.update { it.copy(isGuest = user == null) }
    }

    fun onEvent(event: NewTreeEvent) {
        when (event) {
            is NewTreeEvent.OnFirstNameChanged -> {
                _state.update { it.copy(person = it.person.copy(firstName = event.firstName), firstNameError = null) }
            }

            is NewTreeEvent.OnLastNameChanged -> {
                _state.update { it.copy(person = it.person.copy(lastName = event.lastName), lastNameError = null) }
            }

            is NewTreeEvent.OnBiologicalSexChanged -> {
                _state.update { it.copy(person = it.person.copy(biologicalSex = event.sex), biologicalSexError = null) }
            }

            is NewTreeEvent.OnSexualOrientationChanged -> {
                _state.update {
                    it.copy(
                        person = it.person.copy(sexualOrientation = event.orientation),
                        sexualOrientationError = null,
                    )
                }
            }

            is NewTreeEvent.OnBirthDateSelected -> {
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

            is NewTreeEvent.OnDeathDateSelected -> {
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

            is NewTreeEvent.OnShowBirthDatePicker -> {
                _state.update { it.copy(showBirthDatePicker = event.show) }
            }

            is NewTreeEvent.OnShowDeathDatePicker -> {
                _state.update { it.copy(showDeathDatePicker = event.show) }
            }

            NewTreeEvent.OnCreateTreeClicked -> {
                createTree()
            }

            NewTreeEvent.OnNavigationConsumed -> {
                _state.update { it.copy(navigationEvent = null) }
            }

            NewTreeEvent.OnResetState -> {
                _state.update { currentState ->
                    NewTreeState(isGuest = currentState.isGuest)
                }
            }
        }
    }

    private fun createTree() {
        val personUi = _state.value.person

        var firstNameError: NewTreeState.ValidationError? = null
        var lastNameError: NewTreeState.ValidationError? = null
        var birthDateError: NewTreeState.ValidationError? = null
        var biologicalSexError: NewTreeState.ValidationError? = null
        var sexualOrientationError: NewTreeState.ValidationError? = null

        var isValid = true
        if (personUi.firstName.isBlank()) {
            firstNameError = NewTreeState.ValidationError.EMPTY
            isValid = false
        }
        if (personUi.lastName.isBlank()) {
            lastNameError = NewTreeState.ValidationError.EMPTY
            isValid = false
        }
        if (personUi.birthDateMillis == 0L) {
            birthDateError = NewTreeState.ValidationError.EMPTY
            isValid = false
        }
        if (personUi.biologicalSex == Person.BiologicalSex.UNKNOWN) {
            biologicalSexError = NewTreeState.ValidationError.EMPTY
            isValid = false
        }
        if (personUi.sexualOrientation == Person.SexualOrientation.UNKNOWN) {
            sexualOrientationError = NewTreeState.ValidationError.EMPTY
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
            val person =
                Person(
                    id = "",
                    firstName = personUi.firstName,
                    lastName = personUi.lastName,
                    birthDate = personUi.birthDateMillis,
                    biologicalSex = personUi.biologicalSex,
                    sexualOrientation = personUi.sexualOrientation,
                    deathDate = personUi.deathDateMillis,
                )
            val result = newTreeUseCase(person = person)

            result
                .onSuccess { tree ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            navigationEvent = tree.id,
                        )
                    }
                }.onFailure {
                    _state.update { it.copy(isLoading = false) }
                    // Handle failure if needed
                }
        }
    }
}

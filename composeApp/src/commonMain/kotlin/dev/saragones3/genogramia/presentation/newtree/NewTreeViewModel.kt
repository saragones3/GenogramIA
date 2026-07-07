package dev.saragones3.genogramia.presentation.newtree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.NewTreeUseCase
import dev.saragones3.genogramia.domain.usecase.SearchDiseasesUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class NewTreeViewModel(
    private val newTreeUseCase: NewTreeUseCase,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val searchDiseasesUseCase: SearchDiseasesUseCase,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private val _state = MutableStateFlow(NewTreeState())
    val state: StateFlow<NewTreeState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        val user = checkSessionUseCase()
        _state.update { it.copy(isGuest = user == null) }
    }

    fun onEvent(event: NewTreeEvent) {
        when (event) {
            is NewTreeEvent.OnFirstNameChanged,
            is NewTreeEvent.OnLastNameChanged,
            -> handleBasicInfoEvent(event)

            is NewTreeEvent.OnBiologicalSexChanged,
            is NewTreeEvent.OnSexualOrientationChanged,
            -> handleIdentityEvent(event)

            is NewTreeEvent.OnBirthDateSelected,
            is NewTreeEvent.OnDeathDateSelected,
            is NewTreeEvent.OnDiagnosisDateSelected,
            NewTreeEvent.OnClearBirthDate,
            NewTreeEvent.OnClearDeathDate,
            -> handleDateEvent(event)

            is NewTreeEvent.OnShowBirthDatePicker,
            is NewTreeEvent.OnShowDeathDatePicker,
            is NewTreeEvent.OnShowAddDiseaseSheet,
            is NewTreeEvent.OnShowDiagnosisDatePicker,
            -> handleVisibilityEvent(event)

            is NewTreeEvent.OnDiseaseSearchQueryChanged,
            is NewTreeEvent.OnDiseaseSelected,
            is NewTreeEvent.OnAddDiseaseToHistory,
            is NewTreeEvent.OnRemoveDiseaseFromHistory,
            -> handleDiseaseEvent(event)

            NewTreeEvent.OnCreateTreeClicked,
            NewTreeEvent.OnNavigationConsumed,
            NewTreeEvent.OnResetState,
            -> handleActionEvent(event)
        }
    }

    private fun handleBasicInfoEvent(event: NewTreeEvent) {
        when (event) {
            is NewTreeEvent.OnFirstNameChanged -> {
                _state.update { it.copy(person = it.person.copy(firstName = event.firstName), firstNameError = null) }
            }

            is NewTreeEvent.OnLastNameChanged -> {
                _state.update { it.copy(person = it.person.copy(lastName = event.lastName), lastNameError = null) }
            }

            else -> {
                Unit
            }
        }
    }

    private fun handleIdentityEvent(event: NewTreeEvent) {
        when (event) {
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

            else -> {
                Unit
            }
        }
    }

    private fun handleDateEvent(event: NewTreeEvent) {
        when (event) {
            is NewTreeEvent.OnBirthDateSelected -> updateBirthDate(event.millis, event.pattern)
            is NewTreeEvent.OnDeathDateSelected -> updateDeathDate(event.millis, event.pattern)
            is NewTreeEvent.OnDiagnosisDateSelected -> updateDiagnosisDate(event.millis, event.pattern)
            NewTreeEvent.OnClearBirthDate -> clearBirthDate()
            NewTreeEvent.OnClearDeathDate -> clearDeathDate()
            else -> Unit
        }
    }

    private fun handleVisibilityEvent(event: NewTreeEvent) {
        when (event) {
            is NewTreeEvent.OnShowBirthDatePicker -> _state.update { it.copy(showBirthDatePicker = event.show) }
            is NewTreeEvent.OnShowDeathDatePicker -> _state.update { it.copy(showDeathDatePicker = event.show) }
            is NewTreeEvent.OnShowAddDiseaseSheet -> toggleAddDiseaseSheet(event.show)
            is NewTreeEvent.OnShowDiagnosisDatePicker -> _state.update { it.copy(showDiagnosisDatePicker = event.show) }
            else -> Unit
        }
    }

    private fun handleDiseaseEvent(event: NewTreeEvent) {
        when (event) {
            is NewTreeEvent.OnDiseaseSearchQueryChanged -> updateDiseaseSearchQuery(event.query)
            is NewTreeEvent.OnDiseaseSelected -> _state.update { it.copy(selectedDisease = event.disease) }
            is NewTreeEvent.OnAddDiseaseToHistory -> {}
            is NewTreeEvent.OnRemoveDiseaseFromHistory -> {}
            else -> Unit
        }
    }

    private fun handleActionEvent(event: NewTreeEvent) {
        when (event) {
            NewTreeEvent.OnCreateTreeClicked -> createTree()
            NewTreeEvent.OnNavigationConsumed -> _state.update { it.copy(navigationEvent = null) }
            NewTreeEvent.OnResetState -> resetState()
            else -> Unit
        }
    }

    private fun updateBirthDate(
        millis: Long,
        pattern: String,
    ) {
        val formattedDate = dateFormatter.formatDate(millis, pattern)
        _state.update {
            it.copy(
                person = it.person.copy(birthDateMillis = millis, birthDateText = formattedDate),
            )
        }
    }

    private fun updateDeathDate(
        millis: Long,
        pattern: String,
    ) {
        val formattedDate = dateFormatter.formatDate(millis, pattern)
        _state.update {
            it.copy(
                person = it.person.copy(deathDateMillis = millis, deathDateText = formattedDate),
            )
        }
    }

    private fun clearBirthDate() {
        _state.update {
            it.copy(person = it.person.copy(birthDateMillis = null, birthDateText = ""))
        }
    }

    private fun clearDeathDate() {
        _state.update {
            it.copy(person = it.person.copy(deathDateMillis = null, deathDateText = ""))
        }
    }

    private fun toggleAddDiseaseSheet(show: Boolean) {
        _state.update { it.copy(showAddDiseaseSheet = show) }
        if (!show) {
            resetDiseaseSelection()
        }
    }

    private fun updateDiseaseSearchQuery(query: String) {
        _state.update { it.copy(diseaseSearchQuery = query) }
        searchJob?.cancel()
        if (query.isEmpty()) {
            _state.update { it.copy(diseaseSearchResults = emptyList()) }
        } else {
            searchJob =
                viewModelScope.launch {
                    delay(300.milliseconds)
                    val results = searchDiseasesUseCase(query)
                    _state.update { it.copy(diseaseSearchResults = results) }
                }
        }
    }

    private fun updateDiagnosisDate(
        millis: Long,
        pattern: String,
    ) {
        val formattedDate = dateFormatter.formatDate(millis, pattern)
        _state.update {
            it.copy(diagnosisDateMillis = millis, diagnosisDateText = formattedDate)
        }
    }

    private fun resetState() {
        _state.update { currentState ->
            NewTreeState(isGuest = currentState.isGuest)
        }
    }

    private fun resetDiseaseSelection() {
        _state.update {
            it.copy(
                diseaseSearchQuery = "",
                diseaseSearchResults = emptyList(),
                selectedDisease = null,
                diagnosisDateMillis = null,
                diagnosisDateText = "",
            )
        }
    }

    private fun createTree() {
        val personUi = _state.value.person

        var firstNameError: NewTreeState.ValidationError? = null
        var lastNameError: NewTreeState.ValidationError? = null
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

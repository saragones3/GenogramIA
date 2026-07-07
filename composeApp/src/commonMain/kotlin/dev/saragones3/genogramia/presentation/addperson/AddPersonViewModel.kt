package dev.saragones3.genogramia.presentation.addperson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.model.MedicalCondition
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.AddPersonUseCase
import dev.saragones3.genogramia.domain.usecase.GetPersonUseCase
import dev.saragones3.genogramia.domain.usecase.SearchDiseasesUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePersonUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class AddPersonViewModel(
    private val addPersonUseCase: AddPersonUseCase,
    private val updatePersonUseCase: UpdatePersonUseCase,
    private val getPersonUseCase: GetPersonUseCase,
    private val searchDiseasesUseCase: SearchDiseasesUseCase,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private val _state = MutableStateFlow(AddPersonState())
    val state: StateFlow<AddPersonState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onEvent(event: AddPersonEvent) {
        when (event) {
            is AddPersonEvent.OnFirstNameChanged,
            is AddPersonEvent.OnLastNameChanged,
            -> handleBasicInfoEvent(event)

            is AddPersonEvent.OnBiologicalSexChanged,
            is AddPersonEvent.OnSexualOrientationChanged,
            -> handleIdentityEvent(event)

            is AddPersonEvent.OnBirthDateSelected,
            is AddPersonEvent.OnDeathDateSelected,
            is AddPersonEvent.OnDiagnosisDateSelected,
            AddPersonEvent.OnClearBirthDate,
            AddPersonEvent.OnClearDeathDate,
            -> handleDateEvent(event)

            is AddPersonEvent.OnShowBirthDatePicker,
            is AddPersonEvent.OnShowDeathDatePicker,
            is AddPersonEvent.OnShowAddDiseaseSheet,
            is AddPersonEvent.OnShowDiagnosisDatePicker,
            -> handleVisibilityEvent(event)

            is AddPersonEvent.OnDiseaseSearchQueryChanged,
            is AddPersonEvent.OnDiseaseSelected,
            is AddPersonEvent.OnAddDiseaseToHistory,
            is AddPersonEvent.OnRemoveDiseaseFromHistory,
            -> handleDiseaseEvent(event)

            is AddPersonEvent.OnSaveClicked,
            is AddPersonEvent.Initialize,
            AddPersonEvent.OnResetState,
            -> handleActionEvent(event)
        }
    }

    private fun handleBasicInfoEvent(event: AddPersonEvent) {
        when (event) {
            is AddPersonEvent.OnFirstNameChanged -> {
                _state.update { it.copy(person = it.person.copy(firstName = event.firstName), firstNameError = null) }
            }

            is AddPersonEvent.OnLastNameChanged -> {
                _state.update { it.copy(person = it.person.copy(lastName = event.lastName), lastNameError = null) }
            }

            else -> {
                Unit
            }
        }
    }

    private fun handleIdentityEvent(event: AddPersonEvent) {
        when (event) {
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

            else -> {
                Unit
            }
        }
    }

    private fun handleDateEvent(event: AddPersonEvent) {
        when (event) {
            is AddPersonEvent.OnBirthDateSelected -> updateBirthDate(event.millis, event.pattern)
            is AddPersonEvent.OnDeathDateSelected -> updateDeathDate(event.millis, event.pattern)
            is AddPersonEvent.OnDiagnosisDateSelected -> updateDiagnosisDate(event.millis, event.pattern)
            AddPersonEvent.OnClearBirthDate -> clearBirthDate()
            AddPersonEvent.OnClearDeathDate -> clearDeathDate()
            else -> Unit
        }
    }

    private fun handleVisibilityEvent(event: AddPersonEvent) {
        when (event) {
            is AddPersonEvent.OnShowBirthDatePicker -> {
                _state.update { it.copy(showBirthDatePicker = event.show) }
            }

            is AddPersonEvent.OnShowDeathDatePicker -> {
                _state.update { it.copy(showDeathDatePicker = event.show) }
            }

            is AddPersonEvent.OnShowAddDiseaseSheet -> {
                toggleAddDiseaseSheet(event.show)
            }

            is AddPersonEvent.OnShowDiagnosisDatePicker -> {
                _state.update {
                    it.copy(
                        showDiagnosisDatePicker = event.show,
                    )
                }
            }

            else -> {
                Unit
            }
        }
    }

    private fun handleDiseaseEvent(event: AddPersonEvent) {
        when (event) {
            is AddPersonEvent.OnDiseaseSearchQueryChanged -> updateDiseaseSearchQuery(event.query)
            is AddPersonEvent.OnDiseaseSelected -> _state.update { it.copy(selectedDisease = event.disease) }
            is AddPersonEvent.OnAddDiseaseToHistory -> addDiseaseToHistory(event)
            is AddPersonEvent.OnRemoveDiseaseFromHistory -> removeDiseaseFromHistory(event.diseaseCode)
            else -> Unit
        }
    }

    private fun handleActionEvent(event: AddPersonEvent) {
        when (event) {
            is AddPersonEvent.OnSaveClicked -> {
                savePerson(event.treeId)
            }

            is AddPersonEvent.Initialize -> {
                initialize(
                    event.treeId,
                    event.personId,
                    event.datePattern,
                    event.x,
                    event.y,
                )
            }

            AddPersonEvent.OnResetState -> {
                _state.update { AddPersonState() }
            }

            else -> {
                Unit
            }
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

    private fun addDiseaseToHistory(event: AddPersonEvent.OnAddDiseaseToHistory) {
        val medicalConditionUi =
            MedicalConditionUi(
                diseaseCode = event.disease.code,
                diseaseTitle = event.disease.title,
                chapterCode = event.disease.chapterCode,
                chapterTitle = event.disease.chapterTitle,
                isGenetic = event.disease.isGenetic,
                diagnosisDateMillis = event.dateMillis,
                diagnosisDateText =
                    event.dateMillis?.let {
                        dateFormatter.formatDate(it, event.datePattern)
                    } ?: "",
            )
        _state.update {
            it.copy(
                person =
                    it.person.copy(
                        medicalHistory = it.person.medicalHistory + medicalConditionUi,
                    ),
                showAddDiseaseSheet = false,
            )
        }
        resetDiseaseSelection()
    }

    private fun removeDiseaseFromHistory(diseaseCode: String) {
        _state.update {
            it.copy(
                person =
                    it.person.copy(
                        medicalHistory =
                            it.person.medicalHistory.filter { condition ->
                                condition.diseaseCode != diseaseCode
                            },
                    ),
            )
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

    private fun initialize(
        treeId: String,
        personId: String?,
        datePattern: String,
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
                                    person.birthDate?.let { date ->
                                        dateFormatter.formatDate(date, datePattern)
                                    } ?: "",
                                deathDateMillis = person.deathDate,
                                deathDateText =
                                    person.deathDate?.let { date ->
                                        dateFormatter.formatDate(date, datePattern)
                                    } ?: "",
                                medicalHistory =
                                    person.medicalHistory.map { condition ->
                                        MedicalConditionUi(
                                            diseaseCode = condition.disease.code,
                                            diseaseTitle = condition.disease.title,
                                            chapterCode = condition.disease.chapterCode,
                                            chapterTitle = condition.disease.chapterTitle,
                                            isGenetic = condition.disease.isGenetic,
                                            diagnosisDateMillis = condition.diagnosisDate,
                                            diagnosisDateText =
                                                condition.diagnosisDate?.let { date ->
                                                    dateFormatter.formatDate(date, datePattern)
                                                } ?: "",
                                        )
                                    },
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
                birthDate = personUi.birthDateMillis,
                biologicalSex = personUi.biologicalSex,
                sexualOrientation = personUi.sexualOrientation,
                deathDate = personUi.deathDateMillis,
                medicalHistory =
                    personUi.medicalHistory.map { condition ->
                        MedicalCondition(
                            disease =
                                Disease(
                                    code = condition.diseaseCode,
                                    title = condition.diseaseTitle,
                                    chapterCode = condition.chapterCode,
                                    chapterTitle = condition.chapterTitle,
                                    isGenetic = condition.isGenetic,
                                ),
                            diagnosisDate = condition.diagnosisDateMillis,
                        )
                    },
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

package dev.saragones3.genogramia.presentation.addrelationship

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.usecase.AddRelationshipUseCase
import dev.saragones3.genogramia.domain.usecase.GetPersonUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddRelationshipViewModel(
    private val getPersonUseCase: GetPersonUseCase,
    private val getTreeUseCase: GetTreeUseCase,
    private val addRelationshipUseCase: AddRelationshipUseCase,
    private val dateProvider: DateProvider,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private val _state = MutableStateFlow(AddRelationshipState())
    val state: StateFlow<AddRelationshipState> = _state.asStateFlow()

    private var treeId: String = ""

    fun onResume(
        treeId: String,
        personId1: String,
        personId2: String,
    ) {
        this.treeId = treeId
        _state.value = AddRelationshipState(isLoading = true)
        viewModelScope.launch {
            val tree = getTreeUseCase(treeId)
            val p1 = getPersonUseCase(treeId, personId1)
            val p2 = getPersonUseCase(treeId, personId2)

            val hasConsanguinity =
                if ((tree != null) && (p1 != null) && (p2 != null)) {
                    val p1Parents =
                        tree.relationships
                            .asSequence()
                            .filter {
                                it.type == Relationship.RelationshipType.BIOLOGICAL_OFFSPRING && it.personId2 == p1.id
                            }.map { it.personId1 }
                            .toSet()

                    val p2Parents =
                        tree.relationships
                            .asSequence()
                            .filter {
                                it.type == Relationship.RelationshipType.BIOLOGICAL_OFFSPRING && it.personId2 == p2.id
                            }.map { it.personId1 }
                            .toSet()

                    p1Parents.intersect(p2Parents).isNotEmpty()
                } else {
                    false
                }

            _state.update {
                it.copy(
                    person1 = p1?.toUi(),
                    person2 = p2?.toUi(),
                    isLoading = false,
                    hasConsanguinityRisk = hasConsanguinity,
                )
            }
        }
    }

    fun onEvent(event: AddRelationshipEvent) {
        when (event) {
            is AddRelationshipEvent.OnBondTypeSelected -> {
                _state.update { it.copy(bondType = event.type) }
            }

            is AddRelationshipEvent.OnEmotionalBondSelected -> {
                _state.update { it.copy(emotionalBond = event.bond) }
            }

            is AddRelationshipEvent.OnDateSelected -> {
                _state.update {
                    it.copy(
                        effectiveDate = event.date,
                        effectiveDateFormatted =
                            event.date?.let { date ->
                                dateFormatter.formatDate(date, event.pattern).uppercase()
                            },
                    )
                }
            }

            AddRelationshipEvent.OnConfirmClick -> {
                saveRelationship()
            }

            AddRelationshipEvent.OnSwapPersons -> {
                _state.update {
                    it.copy(
                        person1 = it.person2,
                        person2 = it.person1,
                    )
                }
            }

            AddRelationshipEvent.OnBackClick -> {
                _state.update { it.copy(shouldNavigateBack = true) }
            }

            AddRelationshipEvent.OnNavigationHandled -> {
                _state.update { it.copy(shouldNavigateBack = false) }
            }
        }
    }

    private fun saveRelationship() {
        val currentState = _state.value
        val p1 = currentState.person1 ?: return
        val p2 = currentState.person2 ?: return

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                val relationship =
                    Relationship(
                        id = "${p1.id}_${p2.id}_${dateProvider.nowEpochMilliseconds()}",
                        personId1 = p1.id,
                        personId2 = p2.id,
                        type = currentState.bondType,
                        emotionalBond = currentState.emotionalBond,
                        effectiveDate = currentState.effectiveDate,
                    )
                addRelationshipUseCase(treeId, relationship)
                _state.update { it.copy(isSaving = false, shouldNavigateBack = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    private fun Person.toUi() =
        PersonUi(
            id = id,
            fullName = "$firstName $lastName",
            isFemale = biologicalSex == Person.BiologicalSex.FEMALE,
        )
}

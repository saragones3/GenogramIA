package dev.saragones3.genogramia.presentation.addrelationship

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.usecase.AddRelationshipUseCase
import dev.saragones3.genogramia.domain.usecase.DeleteRelationshipUseCase
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
    private val deleteRelationshipUseCase: DeleteRelationshipUseCase,
    private val dateProvider: DateProvider,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private val _state = MutableStateFlow(AddRelationshipState())
    val state: StateFlow<AddRelationshipState> = _state.asStateFlow()

    private var treeId: String = ""

    fun onResume(
        treeId: String,
        personId1: String?,
        personId2: String?,
        relationshipId: String? = null,
    ) {
        this.treeId = treeId
        // Preserve current state bondType if we are just recomposing,
        // but since this is onResume (entry point), we typically want to load.
        _state.update { it.copy(isLoading = true, relationshipId = relationshipId) }

        viewModelScope.launch {
            try {
                val tree = getTreeUseCase(treeId)

                var finalP1Id = personId1
                var finalP2Id = personId2
                var bondType = Relationship.RelationshipType.MARRIAGE
                var emotionalBond = Relationship.EmotionalBond.POSITIVE
                var effectiveDate: Long? = null

                if (relationshipId != null && tree != null) {
                    val rel = tree.relationships.find { it.id == relationshipId }
                    if (rel != null) {
                        finalP1Id = rel.personId1
                        finalP2Id = rel.personId2
                        bondType = rel.type
                        emotionalBond = rel.emotionalBond
                        effectiveDate = rel.effectiveDate
                    }
                }

                // If we don't have IDs from relationship, use the ones passed from navigation
                val p1 = finalP1Id?.let { getPersonUseCase(treeId, it) }
                val p2 = finalP2Id?.let { getPersonUseCase(treeId, it) }

                val hasConsanguinity =
                    if ((tree != null) && (p1 != null) && (p2 != null)) {
                        val p1Parents =
                            tree.relationships
                                .asSequence()
                                .filter {
                                    it.type == Relationship.RelationshipType.BIOLOGICAL_OFFSPRING &&
                                        it.personId2 == p1.id
                                }.map { it.personId1 }
                                .toSet()

                        val p2Parents =
                            tree.relationships
                                .asSequence()
                                .filter {
                                    it.type == Relationship.RelationshipType.BIOLOGICAL_OFFSPRING &&
                                        it.personId2 == p2.id
                                }.map { it.personId1 }
                                .toSet()

                        p1Parents.intersect(p2Parents).isNotEmpty() || p2Parents.intersect(p1Parents).isNotEmpty()
                    } else {
                        false
                    }

                _state.update {
                    it.copy(
                        person1 = p1?.toUi(),
                        person2 = p2?.toUi(),
                        bondType = bondType,
                        emotionalBond = emotionalBond,
                        effectiveDate = effectiveDate,
                        effectiveDateFormatted =
                            effectiveDate?.let { date ->
                                dateFormatter.formatDate(date, "MM/dd/yyyy").uppercase()
                            },
                        isLoading = false,
                        hasConsanguinityRisk = hasConsanguinity,
                        relationshipId = relationshipId,
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
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

            AddRelationshipEvent.OnDeleteClick -> {
                deleteRelationship()
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
                        id = currentState.relationshipId ?: "${p1.id}_${p2.id}_${dateProvider.nowEpochMilliseconds()}",
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

    private fun deleteRelationship() {
        val currentState = _state.value
        val relationshipId = currentState.relationshipId ?: return

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                deleteRelationshipUseCase(treeId, relationshipId)
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

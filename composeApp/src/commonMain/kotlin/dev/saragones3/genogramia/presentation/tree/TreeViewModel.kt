package dev.saragones3.genogramia.presentation.tree

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePersonUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TreeViewModel(
    private val getTreeUseCase: GetTreeUseCase,
    private val updatePersonUseCase: UpdatePersonUseCase,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private val _state = MutableStateFlow(TreeState())
    val state: StateFlow<TreeState> = _state.asStateFlow()

    fun onEvent(event: TreeEvent) {
        when (event) {
            is TreeEvent.LoadTree -> {
                loadTree(event.id)
            }

            is TreeEvent.OnZoomIn,
            is TreeEvent.OnZoomOut,
            TreeEvent.OnResetViewport,
            is TreeEvent.OnResetToCenter,
            is TreeEvent.OnPan,
            is TreeEvent.OnTransform,
            -> {
                handleTransformation(event)
            }

            TreeEvent.OnNavigationConsumed,
            TreeEvent.OnErrorConsumed,
            -> {
                handleSystemEvents(event)
            }

            is TreeEvent.OnPersonSelected,
            TreeEvent.OnDismissSelection,
            -> {
                handleSelection(event)
            }

            is TreeEvent.OnPersonMove,
            is TreeEvent.OnPersonMoveFinished,
            -> {
                handlePersonMovement(event)
            }
        }
    }

    private fun handleTransformation(event: TreeEvent) {
        when (event) {
            is TreeEvent.OnZoomIn -> {
                _state.update { it.copy(scale = (it.scale + event.delta).coerceAtMost(3f)) }
            }

            is TreeEvent.OnZoomOut -> {
                _state.update { it.copy(scale = (it.scale - event.delta).coerceAtLeast(0.1f)) }
            }

            TreeEvent.OnResetViewport -> {
                _state.update { it.copy(offset = Offset.Zero, scale = 1f) }
            }

            is TreeEvent.OnResetToCenter -> {
                _state.update { it.copy(offset = event.center, scale = 1f) }
            }

            is TreeEvent.OnPan -> {
                _state.update { it.copy(offset = it.offset + event.offset) }
            }

            is TreeEvent.OnTransform -> {
                _state.update {
                    val oldScale = it.scale
                    val newScale = (oldScale * event.zoom).coerceIn(0.1f, 3f)
                    val actualZoom = newScale / oldScale
                    val newOffset = event.centroid + (it.offset - event.centroid) * actualZoom + event.pan
                    it.copy(scale = newScale, offset = newOffset)
                }
            }

            else -> {}
        }
    }

    private fun handleSystemEvents(event: TreeEvent) {
        when (event) {
            TreeEvent.OnNavigationConsumed -> {
                _state.update { it.copy(shouldNavigateBack = false) }
            }

            TreeEvent.OnErrorConsumed -> {
                _state.update { it.copy(error = null) }
            }

            else -> {}
        }
    }

    private fun handleSelection(event: TreeEvent) {
        when (event) {
            is TreeEvent.OnPersonSelected -> {
                _state.update {
                    val currentSelected = it.selectedPersonIds
                    val newSelected =
                        if (currentSelected.contains(event.personId)) {
                            currentSelected - event.personId
                        } else if (currentSelected.size < 2) {
                            currentSelected + event.personId
                        } else {
                            listOf(event.personId)
                        }

                    val relationshipId =
                        if (newSelected.size == 2) {
                            val id1 = newSelected.first()
                            val id2 = newSelected.last()
                            it.tree.relationships
                                .find { rel ->
                                    (rel.personId1 == id1 && rel.personId2 == id2) ||
                                        (rel.personId1 == id2 && rel.personId2 == id1)
                                }?.id
                        } else {
                            null
                        }

                    it.copy(
                        selectedPersonIds = newSelected,
                        selectedRelationshipId = relationshipId,
                    )
                }
            }

            TreeEvent.OnDismissSelection -> {
                _state.update {
                    it.copy(selectedPersonIds = emptyList(), selectedRelationshipId = null)
                }
            }

            else -> {}
        }
    }

    private fun handlePersonMovement(event: TreeEvent) {
        when (event) {
            is TreeEvent.OnPersonMove -> {
                _state.update { state ->
                    val updatedCentralPerson =
                        if (state.tree.centralPerson.id == event.personId) {
                            state.tree.centralPerson.copy(position = state.tree.centralPerson.position + event.delta)
                        } else {
                            state.tree.centralPerson
                        }
                    val updatedPersons =
                        state.tree.persons.map { person ->
                            if (person.id == event.personId) {
                                person.copy(position = person.position + event.delta)
                            } else {
                                person
                            }
                        }
                    state.copy(
                        tree =
                            state.tree.copy(
                                centralPerson = updatedCentralPerson,
                                persons = updatedPersons,
                            ),
                    )
                }
            }

            is TreeEvent.OnPersonMoveFinished -> {
                val personId = event.personId
                val treeId = _state.value.tree.id
                val personNode =
                    if (_state.value.tree.centralPerson.id == personId) {
                        _state.value.tree.centralPerson
                    } else {
                        _state.value.tree.persons
                            .find { it.id == personId }
                    }

                personNode?.let { node ->
                    viewModelScope.launch {
                        try {
                            val tree = getTreeUseCase(treeId)
                            if (tree != null) {
                                val domainPerson =
                                    if (tree.centralPerson.id == personId) {
                                        tree.centralPerson
                                    } else {
                                        tree.persons.find { it.id == personId }
                                    }

                                domainPerson?.let { person ->
                                    val updatedPerson = person.copy(x = node.position.x, y = node.position.y)
                                    updatePersonUseCase(treeId, updatedPerson)
                                }
                            }
                        } catch (_: Exception) {
                            // Error handling could be added here
                        }
                    }
                }
            }

            else -> {}
        }
    }

    private fun loadTree(id: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val tree = getTreeUseCase(id)
                if (tree != null) {
                    _state.update { it.copy(tree = tree.toUi(), isLoading = false) }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = TreeError.NOT_FOUND,
                            shouldNavigateBack = true,
                        )
                    }
                }
            } catch (_: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = TreeError.UNKNOWN,
                        shouldNavigateBack = true,
                    )
                }
            }
        }
    }

    private fun GenogramTree.toUi(): TreeUi {
        val centralPersonId = centralPerson.id
        val centralPersonUi =
            centralPerson.toNodeUi().copy(
                isIndexPerson = true,
            )

        val parents =
            relationships
                .filter {
                    it.type == Relationship.RelationshipType.BIOLOGICAL_OFFSPRING &&
                        it.personId2 == centralPersonId
                }.map { it.personId1 }
                .toSet()

        val partners =
            relationships
                .filter { it.type.isStructural && (it.personId1 == centralPersonId || it.personId2 == centralPersonId) }
                .map { if (it.personId1 == centralPersonId) it.personId2 else it.personId1 }
                .toSet()

        val mappedPersons =
            persons.mapIndexed { index, person ->
                if (person.x != 0f || person.y != 0f) {
                    person.toNodeUi()
                } else {
                    val position =
                        when {
                            parents.contains(person.id) -> {
                                val isMale = person.biologicalSex == Person.BiologicalSex.MALE
                                Offset(if (isMale) -150f else 150f, -250f)
                            }

                            partners.contains(person.id) -> {
                                Offset(250f, 0f)
                            }

                            else -> {
                                val row = (index / 3) + 1
                                val col = (index % 3) - 1
                                Offset(col * 250f, row * 250f)
                            }
                        }
                    person.toNodeUi().copy(position = position)
                }
            }

        return TreeUi(
            id = id,
            name = name,
            centralPerson = centralPersonUi,
            persons = mappedPersons,
            relationships = relationships.map { it.toUi() },
        )
    }

    private fun Relationship.toUi(): RelationshipUi =
        RelationshipUi(
            id = id,
            personId1 = personId1,
            personId2 = personId2,
            type = type,
            emotionalBond = emotionalBond,
        )

    private fun Person.toNodeUi(): PersonNodeUi {
        val birthYear = birthDate.let { dateFormatter.formatDate(it, "yyyy") }
        val deathYear = deathDate?.let { dateFormatter.formatDate(it, "yyyy") } ?: ""

        val age =
            if (birthYear.isNotEmpty()) {
                val start = birthYear.toIntOrNull()
                val end = deathYear.toIntOrNull() ?: 2024 // For simplicity, using 2024 as current year
                if (start != null) (end - start).toString() else ""
            } else {
                ""
            }

        return PersonNodeUi(
            id = id,
            firstName = firstName,
            lastName = lastName,
            biologicalSex = biologicalSex,
            sexualOrientation = sexualOrientation,
            birthDateText = birthYear,
            deathDateText = deathYear,
            age = age,
            isDeceased = deathDate != null,
            position = Offset(x, y),
        )
    }
}

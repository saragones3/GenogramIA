package dev.saragones3.genogramia.presentation.tree

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.usecase.DeletePersonUseCase
import dev.saragones3.genogramia.domain.usecase.DeleteTreeUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePersonUseCase
import dev.saragones3.genogramia.domain.usecase.UpdateTreeUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.yearsUntil
import kotlin.time.Instant
import dev.saragones3.genogramia.domain.usecase.DeletePersonUseCase.DeletePersonError as DPError

class TreeViewModel(
    private val getTreeUseCase: GetTreeUseCase,
    private val deletePersonUseCase: DeletePersonUseCase,
    private val deleteTreeUseCase: DeleteTreeUseCase,
    private val updatePersonUseCase: UpdatePersonUseCase,
    private val updateTreeUseCase: UpdateTreeUseCase,
    private val dateFormatter: DateFormatter,
    private val dateProvider: DateProvider,
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

            is TreeEvent.OnViewportResetPerformed -> {
                _state.update { it.copy(lastLoadedTreeId = event.treeId) }
            }

            TreeEvent.OnDeleteSelectedPersonRequested,
            TreeEvent.OnConfirmDeletePerson,
            TreeEvent.OnDismissDeletePerson,
            TreeEvent.OnDeleteTreeRequested,
            TreeEvent.OnConfirmDeleteTree,
            TreeEvent.OnDismissDeleteTree,
            -> {
                handleDeletion(event)
            }

            TreeEvent.OnToggleEditMode -> {
                _state.update {
                    it.copy(
                        isEditMode = !it.isEditMode,
                        selectedPersonIds = emptyList(),
                        selectedRelationshipId = null,
                    )
                }
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

    private fun handleDeletion(event: TreeEvent) {
        when (event) {
            TreeEvent.OnDeleteSelectedPersonRequested -> {
                val selectedId = _state.value.selectedPersonIds.firstOrNull() ?: return
                val person =
                    _state.value.tree.persons
                        .find { it.id == selectedId } ?: return
                _state.update {
                    it.copy(
                        showDeleteConfirmation = true,
                        personToDeleteName = "${person.firstName} ${person.lastName}",
                    )
                }
            }

            TreeEvent.OnConfirmDeletePerson -> {
                val personId = _state.value.selectedPersonIds.firstOrNull() ?: return
                val treeId = _state.value.tree.id
                _state.update { it.copy(showDeleteConfirmation = false, isLoading = true) }
                viewModelScope.launch {
                    when (val result = deletePersonUseCase(treeId, personId)) {
                        is DeletePersonUseCase.Result.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error =
                                        when (result.type) {
                                            DPError.HAS_DESCENDANTS -> TreeError.HAS_DESCENDANTS
                                            DPError.HAS_FORMAL_RELATIONSHIPS -> TreeError.HAS_FORMAL_RELATIONSHIPS
                                            DPError.TREE_NOT_FOUND -> TreeError.UNKNOWN
                                        },
                                )
                            }
                        }

                        DeletePersonUseCase.Result.Success -> {
                            loadTree(treeId)
                        }
                    }
                }
            }

            TreeEvent.OnDismissDeletePerson -> {
                _state.update { it.copy(showDeleteConfirmation = false, personToDeleteName = null) }
            }

            TreeEvent.OnDeleteTreeRequested -> {
                _state.update { it.copy(showDeleteTreeConfirmation = true) }
            }

            TreeEvent.OnConfirmDeleteTree -> {
                val treeId = _state.value.tree.id
                _state.update { it.copy(showDeleteTreeConfirmation = false, isLoading = true) }
                viewModelScope.launch {
                    try {
                        deleteTreeUseCase(treeId)
                        _state.update { it.copy(isLoading = false, shouldNavigateBack = true) }
                    } catch (_: Exception) {
                        _state.update { it.copy(isLoading = false, error = TreeError.UNKNOWN) }
                    }
                }
            }

            TreeEvent.OnDismissDeleteTree -> {
                _state.update { it.copy(showDeleteTreeConfirmation = false) }
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
        val isSameTree = _state.value.tree.id == id
        _state.update {
            it.copy(
                isLoading = !isSameTree,
                error = null,
                selectedPersonIds = emptyList(),
                selectedRelationshipId = null,
            )
        }
        viewModelScope.launch {
            try {
                val tree = getTreeUseCase(id)
                if (tree != null) {
                    val uiTree = tree.toUi()
                    _state.update {
                        it.copy(
                            tree = uiTree,
                            isLoading = false,
                        )
                    }
                    bakeDefaultPositions(tree, uiTree)
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

    private fun bakeDefaultPositions(
        domainTree: GenogramTree,
        uiTree: TreeUi,
    ) {
        var needsUpdate = false
        var updatedCentralPerson = domainTree.centralPerson
        val updatedPersons = domainTree.persons.toMutableList()

        if (domainTree.centralPerson.x == 0f && domainTree.centralPerson.y == 0f) {
            val uiPos = uiTree.centralPerson.position
            if (uiPos.x != 0f || uiPos.y != 0f) {
                updatedCentralPerson = domainTree.centralPerson.copy(x = uiPos.x, y = uiPos.y)
                needsUpdate = true
            }
        }

        domainTree.persons.forEachIndexed { index, domainPerson ->
            if (domainPerson.x == 0f && domainPerson.y == 0f) {
                val uiPerson = uiTree.persons.find { it.id == domainPerson.id }
                if (uiPerson != null && (uiPerson.position.x != 0f || uiPerson.position.y != 0f)) {
                    updatedPersons[index] = domainPerson.copy(x = uiPerson.position.x, y = uiPerson.position.y)
                    needsUpdate = true
                }
            }
        }

        if (needsUpdate) {
            viewModelScope.launch {
                try {
                    updateTreeUseCase(
                        domainTree.copy(
                            centralPerson = updatedCentralPerson,
                            persons = updatedPersons,
                        ),
                    )
                } catch (_: Exception) {
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
                    it.type.isDescendant && it.personId2 == centralPersonId
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
            dateText = effectiveDate?.let { dateFormatter.formatDate(it, "yyyy") } ?: "",
        )

    private fun Person.toNodeUi(): PersonNodeUi {
        val birthYear = birthDate?.let { dateFormatter.formatDate(it, "yyyy") } ?: ""
        val deathYear = deathDate?.let { dateFormatter.formatDate(it, "yyyy") } ?: ""

        val age =
            birthDate?.let {
                val birthLocalDate =
                    Instant
                        .fromEpochMilliseconds(it)
                        .toLocalDateTime(TimeZone.UTC)
                        .date
                val endLocalDate =
                    (deathDate ?: dateProvider.nowEpochMilliseconds())
                        .let { end -> Instant.fromEpochMilliseconds(end) }
                        .toLocalDateTime(TimeZone.UTC)
                        .date

                birthLocalDate.yearsUntil(endLocalDate).toString()
            } ?: ""

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
            hasMedicalHistory = medicalHistory.isNotEmpty(),
            substanceAbuse = substanceAbuse,
            hasMentalHealthProblem = hasMentalHealthProblem,
            position = Offset(x, y),
        )
    }
}

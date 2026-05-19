package dev.saragones3.genogramia.presentation.tree

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TreeViewModel(
    private val getTreeUseCase: GetTreeUseCase,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private val _state = MutableStateFlow(TreeState())
    val state: StateFlow<TreeState> = _state.asStateFlow()

    fun onEvent(event: TreeEvent) {
        when (event) {
            is TreeEvent.LoadTree -> {
                loadTree(event.id)
            }

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
                    it.copy(
                        scale = newScale,
                        offset = newOffset,
                    )
                }
            }

            TreeEvent.OnNavigationConsumed -> {
                _state.update { it.copy(shouldNavigateBack = false) }
            }

            TreeEvent.OnErrorConsumed -> {
                _state.update { it.copy(error = null) }
            }

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
                    it.copy(selectedPersonIds = newSelected)
                }
            }

            TreeEvent.OnDismissSelection -> {
                _state.update { it.copy(selectedPersonIds = emptyList()) }
            }

            TreeEvent.OnAddRelationship -> {
                // To be implemented in US-020
            }

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
        val centralPersonUi =
            centralPerson.toNodeUi().copy(
                position = Offset.Zero,
                isIndexPerson = true,
            )
        val mappedPersons =
            persons.mapIndexed { index, person ->
                val row = (index / 3) + 1
                val col = (index % 3) - 1
                person.toNodeUi().copy(position = Offset(col * 250f, row * 250f))
            }
        return TreeUi(
            id = id,
            name = name,
            centralPerson = centralPersonUi,
            persons = mappedPersons,
        )
    }

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
        )
    }
}

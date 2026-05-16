package dev.saragones3.genogramia.presentation.tree

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TreeViewModel(
    private val getTreeUseCase: GetTreeUseCase,
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
                    val newScale = (it.scale * event.zoom).coerceIn(0.1f, 3f)
                    it.copy(
                        scale = newScale,
                        offset = it.offset + event.pan,
                    )
                }
            }

            TreeEvent.OnNavigationConsumed -> {
                _state.update { it.copy(shouldNavigateBack = false) }
            }

            TreeEvent.OnErrorConsumed -> {
                _state.update { it.copy(error = null) }
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
            } catch (e: Exception) {
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
        val centralPersonUi = centralPerson.toUi().copy(position = Offset.Zero)
        val mappedPersons =
            persons.mapIndexed { index, person ->
                val row = (index / 3) + 1
                val col = (index % 3) - 1
                person.toUi().copy(position = Offset(col * 250f, row * 250f))
            }
        return TreeUi(
            id = id,
            name = name,
            centralPerson = centralPersonUi,
            persons = mappedPersons,
        )
    }

    private fun Person.toUi(): PersonUi {
        val birthYear = birthDate?.let { extractYear(it) }
        val deathYear = deathDate?.let { extractYear(it) }

        val dateText =
            when {
                (birthYear != null && deathYear != null) -> "$birthYear - $deathYear"
                birthYear != null -> "B. $birthYear"
                else -> ""
            }

        return PersonUi(
            id = id,
            firstName = firstName,
            lastName = lastName,
            biologicalSex = biologicalSex,
            dateText = dateText,
        )
    }

    private fun extractYear(date: String): String = date.split("/").lastOrNull() ?: date
}

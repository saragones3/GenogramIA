package dev.saragones3.genogramia.presentation.authenticatedhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreesUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider
import dev.saragones3.genogramia.presentation.model.GenogramTreeUiModel
import dev.saragones3.genogramia.presentation.util.UiText
import dev.saragones3.genogramia.presentation.util.formatLastUpdated
import genogramia.composeapp.generated.resources.Res
import genogramia.composeapp.generated.resources.new_tree_name
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthenticatedHomeViewModel(
    private val checkSession: CheckSessionUseCase,
    private val getTrees: GetTreesUseCase,
    private val dateProvider: DateProvider,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private var allTrees = listOf<GenogramTree>()

    private val _uiState = MutableStateFlow(AuthenticatedHomeUiState())
    val uiState: StateFlow<AuthenticatedHomeUiState> = _uiState.asStateFlow()

    fun onResume() {
        _uiState.update { it.copy(userName = checkSession()?.displayName ?: "User") }
        loadTrees()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        updateFilteredTrees()
    }

    private fun loadTrees() {
        viewModelScope.launch {
            if (allTrees.isEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
            }
            allTrees = getTrees()
            updateFilteredTrees()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun updateFilteredTrees() {
        val query = _uiState.value.searchQuery
        val filtered =
            if (query.isBlank()) {
                allTrees
            } else {
                allTrees.filter { it.name.contains(query, ignoreCase = true) }
            }

        _uiState.update {
            it.copy(
                trees =
                    filtered.map { tree ->
                        GenogramTreeUiModel(
                            id = tree.id,
                            title = UiText.Resource(Res.string.new_tree_name, arrayOf(tree.name)),
                            ancestorCount = tree.ancestorCount,
                            lastUpdated =
                                tree.formatLastUpdated(
                                    now = dateProvider.now(),
                                    format = { millis, pattern ->
                                        dateFormatter.formatDate(millis, pattern)
                                    },
                                ),
                            isPrimary = tree.id == "1",
                        )
                    },
            )
        }
    }
}

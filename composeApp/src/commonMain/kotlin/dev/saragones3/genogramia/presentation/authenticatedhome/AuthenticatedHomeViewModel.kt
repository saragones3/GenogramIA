package dev.saragones3.genogramia.presentation.authenticatedhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreesUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider
import dev.saragones3.genogramia.presentation.model.GenogramTreeUiModel
import dev.saragones3.genogramia.presentation.util.formatLastUpdated
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthenticatedHomeViewModel(
    private val checkSession: CheckSessionUseCase,
    private val getTrees: GetTreesUseCase,
    private val dateProvider: DateProvider,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allTrees = listOf<GenogramTree>()
    private val _trees = MutableStateFlow<List<GenogramTreeUiModel>>(emptyList())
    val trees: StateFlow<List<GenogramTreeUiModel>> = _trees.asStateFlow()

    fun onResume() {
        _userName.value = checkSession()?.displayName ?: "User"
        loadTrees()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        updateFilteredTrees()
    }

    private fun loadTrees() {
        viewModelScope.launch {
            allTrees = getTrees()
            updateFilteredTrees()
        }
    }

    private fun updateFilteredTrees() {
        val query = _searchQuery.value
        val filtered =
            if (query.isBlank()) {
                allTrees
            } else {
                allTrees.filter { it.name.contains(query, ignoreCase = true) }
            }

        _trees.value =
            filtered.map { tree ->
                GenogramTreeUiModel(
                    id = tree.id,
                    title = tree.name,
                    ancestorCount = tree.ancestorCount,
                    lastUpdated = tree.formatLastUpdated(
                        now = dateProvider.now(),
                        format = { millis, pattern ->
                            dateFormatter.formatDate(millis, pattern)
                        },
                    ),
                    isPrimary = tree.id == "1",
                )
            }
    }
}

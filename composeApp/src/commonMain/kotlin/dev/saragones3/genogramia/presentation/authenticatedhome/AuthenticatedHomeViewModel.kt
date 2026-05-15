package dev.saragones3.genogramia.presentation.authenticatedhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthenticatedHomeViewModel(
    private val checkSession: CheckSessionUseCase,
    private val getTrees: GetTreesUseCase,
) : ViewModel() {
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allTrees = listOf<GenogramTree>()
    private val _trees = MutableStateFlow<List<GenogramTree>>(emptyList())
    val trees: StateFlow<List<GenogramTree>> = _trees.asStateFlow()

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
        _trees.value =
            if (query.isBlank()) {
                allTrees
            } else {
                allTrees.filter { it.name.contains(query, ignoreCase = true) }
            }
    }
}

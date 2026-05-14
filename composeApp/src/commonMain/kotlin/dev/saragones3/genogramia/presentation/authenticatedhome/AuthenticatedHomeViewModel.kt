package dev.saragones3.genogramia.presentation.authenticatedhome

import androidx.lifecycle.ViewModel
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthenticatedHomeViewModel(
    checkSession: CheckSessionUseCase,
) : ViewModel() {
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allTrees = listOf<GenogramTree>()
    private val _trees = MutableStateFlow<List<GenogramTree>>(emptyList())
    val trees: StateFlow<List<GenogramTree>> = _trees.asStateFlow()

    init {
        _userName.value = checkSession()?.displayName ?: "User"
        loadTrees()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        updateFilteredTrees()
    }

    private fun loadTrees() {
        // Mock data as requested
        allTrees =
            listOf(
                GenogramTree("1", "Smith Family", 1240, "2 days ago", Person()),
                GenogramTree("2", "Maternal Lineage", 8, "1 week ago", Person()),
                GenogramTree("3", "Paternal Lineage", 12, "2 weeks ago", Person()),
            )
        updateFilteredTrees()
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

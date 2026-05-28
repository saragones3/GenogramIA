package dev.saragones3.genogramia.presentation.authenticatedhome

import dev.saragones3.genogramia.presentation.model.GenogramTreeUiModel

data class AuthenticatedHomeUiState(
    val userName: String = "",
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val trees: List<GenogramTreeUiModel> = emptyList(),
)

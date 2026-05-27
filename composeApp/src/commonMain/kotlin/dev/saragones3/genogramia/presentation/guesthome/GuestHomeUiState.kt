package dev.saragones3.genogramia.presentation.guesthome

import dev.saragones3.genogramia.presentation.model.GenogramTreeUiModel

data class GuestHomeUiState(
    val searchQuery: String = "",
    val trees: List<GenogramTreeUiModel> = emptyList(),
)

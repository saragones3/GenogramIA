package dev.saragones3.genogramia.presentation.model

import dev.saragones3.genogramia.presentation.util.UiText

data class GenogramTreeUiModel(
    val id: String,
    val title: UiText,
    val ancestorCount: Int,
    val lastUpdated: UiText,
    val isPrimary: Boolean = false,
)

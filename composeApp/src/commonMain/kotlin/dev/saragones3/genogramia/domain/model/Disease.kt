package dev.saragones3.genogramia.domain.model

data class Disease(
    val code: String,
    val title: String,
    val chapterCode: String,
    val chapterTitle: String,
    val isGenetic: Boolean,
)

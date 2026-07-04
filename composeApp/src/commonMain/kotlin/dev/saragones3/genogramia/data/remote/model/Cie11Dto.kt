package dev.saragones3.genogramia.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cie11ChapterDto(
    @SerialName("codigo")
    val code: String,
    @SerialName("titulo")
    val title: Cie11TitleDto,
    @SerialName("lastSync")
    val lastSync: String,
    @SerialName("subcategorias")
    val subcategories: List<Cie11SubcategoryDto>,
)

@Serializable
data class Cie11TitleDto(
    val es: String = "",
    val en: String = "",
)

@Serializable
data class Cie11SubcategoryDto(
    @SerialName("codigo")
    val code: String,
    @SerialName("titulo")
    val title: Cie11TitleDto,
    @SerialName("isGenetic")
    val isGenetic: Boolean,
)

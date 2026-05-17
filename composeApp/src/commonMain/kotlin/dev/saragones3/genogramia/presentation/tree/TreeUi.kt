package dev.saragones3.genogramia.presentation.tree

import androidx.compose.ui.geometry.Offset
import dev.saragones3.genogramia.domain.model.Person

data class TreeUi(
    val id: String = "",
    val name: String = "",
    val centralPerson: PersonNodeUi = PersonNodeUi(),
    val persons: List<PersonNodeUi> = emptyList(),
)

data class PersonNodeUi(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val biologicalSex: Person.BiologicalSex = Person.BiologicalSex.UNKNOWN,
    val sexualOrientation: Person.SexualOrientation = Person.SexualOrientation.UNKNOWN,
    val birthDateText: String = "",
    val deathDateText: String = "",
    val age: String = "",
    val isDeceased: Boolean = false,
    val isIndexPerson: Boolean = false,
    val position: Offset = Offset.Zero,
)

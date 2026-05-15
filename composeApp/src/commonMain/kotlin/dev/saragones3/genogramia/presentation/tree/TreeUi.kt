package dev.saragones3.genogramia.presentation.tree

import dev.saragones3.genogramia.domain.model.Person

data class TreeUi(
    val id: String = "",
    val name: String = "",
    val centralPerson: PersonUi = PersonUi(),
)

data class PersonUi(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val biologicalSex: Person.BiologicalSex = Person.BiologicalSex.UNKNOWN,
    val dateText: String = "",
)

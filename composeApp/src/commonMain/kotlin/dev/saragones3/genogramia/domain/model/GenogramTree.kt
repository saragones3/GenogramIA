package dev.saragones3.genogramia.domain.model

data class GenogramTree(
    val id: String,
    val name: String,
    val ancestorCount: Int,
    val lastUpdated: String,
    val centralPerson: Person,
    val persons: List<Person> = emptyList(),
)

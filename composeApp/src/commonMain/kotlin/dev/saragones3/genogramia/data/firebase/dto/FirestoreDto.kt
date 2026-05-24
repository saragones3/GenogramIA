package dev.saragones3.genogramia.data.firebase.dto

import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import kotlinx.serialization.Serializable

@Serializable
data class PersonDto(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: Long = 0L,
    val biologicalSex: String = Person.BiologicalSex.UNKNOWN.name,
    val sexualOrientation: String = Person.SexualOrientation.UNKNOWN.name,
    val deathDate: Long? = null,
    val x: Float = 0f,
    val y: Float = 0f,
)

@Serializable
data class RelationshipDto(
    val id: String = "",
    val personId1: String = "",
    val personId2: String = "",
    val type: String = Relationship.RelationshipType.MARRIAGE.name,
    val emotionalBond: String = Relationship.EmotionalBond.POSITIVE.name,
    val effectiveDate: Long? = null,
)

@Serializable
data class GenogramTreeDto(
    val id: String = "",
    val name: String = "",
    val ancestorCount: Int = 0,
    val lastUpdated: String = "",
    val centralPerson: PersonDto = PersonDto(),
    val persons: List<PersonDto> = emptyList(),
    val relationships: List<RelationshipDto> = emptyList(),
)

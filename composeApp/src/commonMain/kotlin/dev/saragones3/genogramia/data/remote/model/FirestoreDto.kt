package dev.saragones3.genogramia.data.remote.model

import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiseaseDto(
    val code: String = "",
    val title: String = "",
    val chapterCode: String = "",
    val chapterTitle: String = "",
    @SerialName("genetic")
    val isGenetic: Boolean = false,
)

@Serializable
data class MedicalConditionDto(
    val disease: DiseaseDto = DiseaseDto(),
    val diagnosisDate: Long? = null,
)

@Serializable
data class PersonDto(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: Long? = null,
    val deathDate: Long? = null,
    val biologicalSex: String = Person.BiologicalSex.UNKNOWN.name,
    val sexualOrientation: String = Person.SexualOrientation.UNKNOWN.name,
    val medicalHistory: List<MedicalConditionDto> = emptyList(),
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
    val ancestorCount: Int = 0,
    val lastUpdated: String = "",
    val centralPerson: PersonDto = PersonDto(),
    val persons: List<PersonDto> = emptyList(),
    val relationships: List<RelationshipDto> = emptyList(),
)

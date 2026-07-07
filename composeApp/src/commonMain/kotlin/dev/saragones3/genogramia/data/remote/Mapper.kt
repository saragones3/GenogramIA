package dev.saragones3.genogramia.data.remote

import dev.saragones3.genogramia.data.remote.model.DiseaseDto
import dev.saragones3.genogramia.data.remote.model.GenogramTreeDto
import dev.saragones3.genogramia.data.remote.model.MedicalConditionDto
import dev.saragones3.genogramia.data.remote.model.PersonDto
import dev.saragones3.genogramia.data.remote.model.RelationshipDto
import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.MedicalCondition
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship

fun Disease.toDto() =
    DiseaseDto(
        code = code,
        title = title,
        chapterCode = chapterCode,
        chapterTitle = chapterTitle,
        isGenetic = isGenetic,
    )

fun DiseaseDto.toDomain() =
    Disease(
        code = code,
        title = title,
        chapterCode = chapterCode,
        chapterTitle = chapterTitle,
        isGenetic = isGenetic,
    )

fun MedicalCondition.toDto() =
    MedicalConditionDto(
        disease = disease.toDto(),
        diagnosisDate = diagnosisDate,
    )

fun MedicalConditionDto.toDomain() =
    MedicalCondition(
        disease = disease.toDomain(),
        diagnosisDate = diagnosisDate,
    )

fun Person.toDto() =
    PersonDto(
        id = id,
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        biologicalSex = biologicalSex.name,
        sexualOrientation = sexualOrientation.name,
        deathDate = deathDate,
        medicalHistory = medicalHistory.map { it.toDto() },
        x = x,
        y = y,
    )

fun PersonDto.toDomain() =
    Person(
        id = id,
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        biologicalSex =
            runCatching {
                Person.BiologicalSex.valueOf(
                    biologicalSex,
                )
            }.getOrDefault(Person.BiologicalSex.UNKNOWN),
        sexualOrientation =
            runCatching {
                Person.SexualOrientation.valueOf(sexualOrientation)
            }.getOrDefault(Person.SexualOrientation.UNKNOWN),
        deathDate = deathDate,
        medicalHistory = medicalHistory.map { it.toDomain() },
        x = x,
        y = y,
    )

fun Relationship.toDto() =
    RelationshipDto(
        id = id,
        personId1 = personId1,
        personId2 = personId2,
        type = type.name,
        emotionalBond = emotionalBond.name,
        effectiveDate = effectiveDate,
    )

fun RelationshipDto.toDomain() =
    Relationship(
        id = id,
        personId1 = personId1,
        personId2 = personId2,
        type =
            runCatching {
                Relationship.RelationshipType.valueOf(
                    type,
                )
            }.getOrDefault(Relationship.RelationshipType.MARRIAGE),
        emotionalBond =
            runCatching {
                Relationship.EmotionalBond.valueOf(emotionalBond)
            }.getOrDefault(Relationship.EmotionalBond.POSITIVE),
        effectiveDate = effectiveDate,
    )

fun GenogramTree.toDto() =
    GenogramTreeDto(
        id = id,
        ancestorCount = ancestorCount,
        lastUpdated = lastUpdated,
        centralPerson = centralPerson.toDto(),
        persons = persons.map { it.toDto() },
        relationships = relationships.map { it.toDto() },
    )

fun GenogramTreeDto.toDomain() =
    GenogramTree(
        id = id,
        ancestorCount = ancestorCount,
        lastUpdated = lastUpdated,
        centralPerson = centralPerson.toDomain(),
        persons = persons.map { it.toDomain() },
        relationships = relationships.map { it.toDomain() },
    )

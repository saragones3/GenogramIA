package dev.saragones3.genogramia.data.firebase

import dev.saragones3.genogramia.data.firebase.dto.GenogramTreeDto
import dev.saragones3.genogramia.data.firebase.dto.PersonDto
import dev.saragones3.genogramia.data.firebase.dto.RelationshipDto
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship

fun Person.toDto() =
    PersonDto(
        id = id,
        firstName = firstName,
        lastName = lastName,
        birthDate = birthDate,
        biologicalSex = biologicalSex.name,
        sexualOrientation = sexualOrientation.name,
        deathDate = deathDate,
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
        name = name,
        ancestorCount = ancestorCount,
        lastUpdated = lastUpdated,
        centralPerson = centralPerson.toDto(),
        persons = persons.map { it.toDto() },
        relationships = relationships.map { it.toDto() },
    )

fun GenogramTreeDto.toDomain() =
    GenogramTree(
        id = id,
        name = name,
        ancestorCount = ancestorCount,
        lastUpdated = lastUpdated,
        centralPerson = centralPerson.toDomain(),
        persons = persons.map { it.toDomain() },
        relationships = relationships.map { it.toDomain() },
    )

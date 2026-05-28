package dev.saragones3.genogramia.data.firebase

import dev.saragones3.genogramia.data.firebase.dto.GenogramTreeDto
import dev.saragones3.genogramia.data.firebase.dto.PersonDto
import dev.saragones3.genogramia.data.firebase.dto.RelationshipDto
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import kotlin.test.Test
import kotlin.test.assertEquals

class MapperTest {
    @Test
    fun `GIVEN person WHEN mapping to dto THEN fields are mapped correctly`() {
        val person =
            Person(
                id = "p1",
                firstName = "John",
                lastName = "Doe",
                birthDate = 1000L,
                biologicalSex = Person.BiologicalSex.MALE,
                sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                deathDate = 2000L,
                x = 10f,
                y = 20f,
            )

        val dto = person.toDto()

        assertEquals(person.id, dto.id)
        assertEquals(person.firstName, dto.firstName)
        assertEquals(person.lastName, dto.lastName)
        assertEquals(person.birthDate, dto.birthDate)
        assertEquals(person.biologicalSex.name, dto.biologicalSex)
        assertEquals(person.sexualOrientation.name, dto.sexualOrientation)
        assertEquals(person.deathDate, dto.deathDate)
        assertEquals(person.x, dto.x)
        assertEquals(person.y, dto.y)
    }

    @Test
    fun `GIVEN person dto WHEN mapping to domain THEN fields are mapped correctly`() {
        val dto =
            PersonDto(
                id = "p1",
                firstName = "John",
                lastName = "Doe",
                birthDate = 1000L,
                biologicalSex = "MALE",
                sexualOrientation = "HETEROSEXUAL",
                deathDate = 2000L,
                x = 10f,
                y = 20f,
            )

        val person = dto.toDomain()

        assertEquals(dto.id, person.id)
        assertEquals(dto.firstName, person.firstName)
        assertEquals(dto.lastName, person.lastName)
        assertEquals(dto.birthDate, person.birthDate)
        assertEquals(Person.BiologicalSex.MALE, person.biologicalSex)
        assertEquals(Person.SexualOrientation.HETEROSEXUAL, person.sexualOrientation)
        assertEquals(dto.deathDate, person.deathDate)
        assertEquals(dto.x, person.x)
        assertEquals(dto.y, person.y)
    }

    @Test
    fun `GIVEN person dto with invalid enums WHEN mapping to domain THEN defaults are used`() {
        val dto =
            PersonDto(
                biologicalSex = "INVALID",
                sexualOrientation = "INVALID",
            )

        val person = dto.toDomain()

        assertEquals(Person.BiologicalSex.UNKNOWN, person.biologicalSex)
        assertEquals(Person.SexualOrientation.UNKNOWN, person.sexualOrientation)
    }

    @Test
    fun `GIVEN relationship WHEN mapping to dto THEN fields are mapped correctly`() {
        val relationship =
            Relationship(
                id = "r1",
                personId1 = "p1",
                personId2 = "p2",
                type = Relationship.RelationshipType.MARRIAGE,
                emotionalBond = Relationship.EmotionalBond.INTIMATE,
                effectiveDate = 5000L,
            )

        val dto = relationship.toDto()

        assertEquals(relationship.id, dto.id)
        assertEquals(relationship.personId1, dto.personId1)
        assertEquals(relationship.personId2, dto.personId2)
        assertEquals(relationship.type.name, dto.type)
        assertEquals(relationship.emotionalBond.name, dto.emotionalBond)
        assertEquals(relationship.effectiveDate, dto.effectiveDate)
    }

    @Test
    fun `GIVEN relationship dto WHEN mapping to domain THEN fields are mapped correctly`() {
        val dto =
            RelationshipDto(
                id = "r1",
                personId1 = "p1",
                personId2 = "p2",
                type = "DIVORCE",
                emotionalBond = "CONFLICTUAL",
                effectiveDate = 5000L,
            )

        val relationship = dto.toDomain()

        assertEquals(dto.id, relationship.id)
        assertEquals(dto.personId1, relationship.personId1)
        assertEquals(dto.personId2, relationship.personId2)
        assertEquals(Relationship.RelationshipType.DIVORCE, relationship.type)
        assertEquals(Relationship.EmotionalBond.CONFLICTUAL, relationship.emotionalBond)
        assertEquals(dto.effectiveDate, relationship.effectiveDate)
    }

    @Test
    fun `GIVEN relationship dto with invalid enums WHEN mapping to domain THEN defaults are used`() {
        val dto =
            RelationshipDto(
                type = "INVALID",
                emotionalBond = "INVALID",
            )

        val relationship = dto.toDomain()

        assertEquals(Relationship.RelationshipType.MARRIAGE, relationship.type)
        assertEquals(Relationship.EmotionalBond.POSITIVE, relationship.emotionalBond)
    }

    @Test
    fun `GIVEN genogram tree WHEN mapping to dto THEN fields are mapped correctly`() {
        val centralPerson = Person(id = "cp", firstName = "Central", lastName = "P", birthDate = 0L)
        val person1 = Person(id = "p1", firstName = "P1", lastName = "L1", birthDate = 0L)
        val relationship1 =
            Relationship(id = "r1", personId1 = "cp", personId2 = "p1", type = Relationship.RelationshipType.MARRIAGE)

        val tree =
            GenogramTree(
                id = "t1",
                ancestorCount = 2,
                lastUpdated = "2024-05-15",
                centralPerson = centralPerson,
                persons = listOf(person1),
                relationships = listOf(relationship1),
            )

        val dto = tree.toDto()

        assertEquals(tree.id, dto.id)
        assertEquals(tree.ancestorCount, dto.ancestorCount)
        assertEquals(tree.lastUpdated, dto.lastUpdated)
        assertEquals(tree.centralPerson.id, dto.centralPerson.id)
        assertEquals(1, dto.persons.size)
        assertEquals(person1.id, dto.persons[0].id)
        assertEquals(1, dto.relationships.size)
        assertEquals(relationship1.id, dto.relationships[0].id)
    }

    @Test
    fun `GIVEN genogram tree dto WHEN mapping to domain THEN fields are mapped correctly`() {
        val centralPersonDto = PersonDto(id = "cp", firstName = "Central", lastName = "P")
        val person1Dto = PersonDto(id = "p1", firstName = "P1")
        val relationship1Dto = RelationshipDto(id = "r1", personId1 = "cp", personId2 = "p1")

        val dto =
            GenogramTreeDto(
                id = "t1",
                ancestorCount = 2,
                lastUpdated = "2024-05-15",
                centralPerson = centralPersonDto,
                persons = listOf(person1Dto),
                relationships = listOf(relationship1Dto),
            )

        val tree = dto.toDomain()

        assertEquals(dto.id, tree.id)
        assertEquals("P", tree.name)
        assertEquals(dto.ancestorCount, tree.ancestorCount)
        assertEquals(dto.lastUpdated, tree.lastUpdated)
        assertEquals(dto.centralPerson.id, tree.centralPerson.id)
        assertEquals(1, tree.persons.size)
        assertEquals(person1Dto.id, tree.persons[0].id)
        assertEquals(1, tree.relationships.size)
        assertEquals(relationship1Dto.id, tree.relationships[0].id)
    }
}

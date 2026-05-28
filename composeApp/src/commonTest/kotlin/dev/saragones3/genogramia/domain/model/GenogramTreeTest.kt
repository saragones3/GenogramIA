package dev.saragones3.genogramia.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class GenogramTreeTest {
    private val centralPerson = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)

    @Test
    fun `GIVEN tree with central person WHEN getting name THEN returns lastName of central person`() {
        val tree =
            GenogramTree(
                id = "tree-1",
                ancestorCount = 0,
                lastUpdated = "",
                centralPerson = centralPerson,
            )
        assertEquals("Doe", tree.name)
    }

    @Test
    fun `GIVEN tree with only central person WHEN calculating ancestor count THEN returns 0`() {
        val tree =
            GenogramTree(
                id = "tree-1",
                ancestorCount = 0,
                lastUpdated = "",
                centralPerson = centralPerson,
            )
        assertEquals(0, tree.calculateAncestorCount())
    }

    @Test
    fun `GIVEN central person with one parent WHEN calculating ancestor count THEN returns 1`() {
        val p2 = Person(id = "p2", firstName = "Father", lastName = "Doe", birthDate = 0L)
        val tree =
            GenogramTree(
                id = "tree-1",
                ancestorCount = 0,
                lastUpdated = "",
                centralPerson = centralPerson,
                persons = listOf(p2),
                relationships =
                    listOf(
                        Relationship(
                            id = "r1",
                            personId1 = "p2",
                            personId2 = "p1",
                            type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                        ),
                    ),
            )
        assertEquals(1, tree.calculateAncestorCount())
    }

    @Test
    fun `GIVEN multiple generations WHEN calculating ancestor count THEN returns correct count`() {
        val father = Person(id = "f", firstName = "Father", lastName = "Doe", birthDate = 0L)
        val mother = Person(id = "m", firstName = "Mother", lastName = "Smith", birthDate = 0L)
        val gFather = Person(id = "gf", firstName = "Grandpa", lastName = "Doe", birthDate = 0L)

        val tree =
            GenogramTree(
                id = "tree-1",
                ancestorCount = 0,
                lastUpdated = "",
                centralPerson = centralPerson,
                persons = listOf(father, mother, gFather),
                relationships =
                    listOf(
                        Relationship("r1", "f", "p1", Relationship.RelationshipType.BIOLOGICAL_OFFSPRING),
                        Relationship("r2", "m", "p1", Relationship.RelationshipType.BIOLOGICAL_OFFSPRING),
                        Relationship("r3", "gf", "f", Relationship.RelationshipType.BIOLOGICAL_OFFSPRING),
                    ),
            )
        // Ancestors: father, mother, grandpa = 3
        assertEquals(3, tree.calculateAncestorCount())
    }

    @Test
    fun `GIVEN tree with non descendant relationships WHEN calculating ancestor count THEN does not count them`() {
        val spouse = Person(id = "s", firstName = "Spouse", lastName = "Doe", birthDate = 0L)
        val tree =
            GenogramTree(
                id = "tree-1",
                ancestorCount = 0,
                lastUpdated = "",
                centralPerson = centralPerson,
                persons = listOf(spouse),
                relationships =
                    listOf(
                        Relationship("r1", "p1", "s", Relationship.RelationshipType.MARRIAGE),
                    ),
            )
        assertEquals(0, tree.calculateAncestorCount())
    }
}

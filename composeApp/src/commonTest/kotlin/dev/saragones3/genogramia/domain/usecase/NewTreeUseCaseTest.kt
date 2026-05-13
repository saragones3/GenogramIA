package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.util.DateProvider
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NewTreeUseCaseTest {
    private lateinit var repository: FakeTreeRepository
    private lateinit var useCase: NewTreeUseCase

    private val fakeDateProvider =
        object : DateProvider {
            override fun nowFormatted(): String = "1970-01-02T10:17:36:Z"

            override fun nowEpochMilliseconds(): Long = 123456789L
        }

    @BeforeTest
    fun setup() {
        repository = FakeTreeRepository()
        useCase = NewTreeUseCase(repository, fakeDateProvider)
    }

    @Test
    fun `when first name is blank validation fails`() =
        runTest {
            val result =
                useCase(
                    Person(
                        id = "",
                        firstName = "",
                        lastName = "Doe",
                        biologicalSex = Person.BiologicalSex.MALE,
                        sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                    ),
                )

            assertTrue(result.isFailure)
            assertEquals("First name and last name are required", result.exceptionOrNull()?.message)
        }

    @Test
    fun `when last name is blank validation fails`() =
        runTest {
            val result =
                useCase(
                    Person(
                        id = "",
                        firstName = "John",
                        lastName = " ",
                        biologicalSex = Person.BiologicalSex.MALE,
                        sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                    ),
                )

            assertTrue(result.isFailure)
            assertEquals("First name and last name are required", result.exceptionOrNull()?.message)
        }

    @Test
    fun `when data is valid creation succeeds`() =
        runTest {
            val result =
                useCase(
                    Person(
                        id = "",
                        firstName = "John",
                        lastName = "Doe",
                        biologicalSex = Person.BiologicalSex.MALE,
                        sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                        birthDate = "1990-01-01",
                    ),
                )

            assertTrue(result.isSuccess)
            val tree = result.getOrNull()
            assertEquals("John Doe Lineage", tree?.name)
            assertEquals(1, tree?.ancestorCount)
            assertEquals("John", tree?.centralPerson?.firstName)
            assertEquals(Person.BiologicalSex.MALE, tree?.centralPerson?.biologicalSex)
            assertEquals(Person.SexualOrientation.HETEROSEXUAL, tree?.centralPerson?.sexualOrientation)
            assertEquals("tree-123456789", tree?.id)
            assertEquals("1970-01-02T10:17:36:Z", tree?.lastUpdated)
        }
}

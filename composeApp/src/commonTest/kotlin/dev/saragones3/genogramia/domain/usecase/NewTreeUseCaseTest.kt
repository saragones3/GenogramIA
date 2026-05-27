package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.fakes.FakeDateProvider
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NewTreeUseCaseTest {
    private lateinit var repository: FakeTreeRepository
    private val dateFormatter = DateFormatter()
    private val fakeDateProvider =
        FakeDateProvider().apply {
            currentTimeMillis = 1778716800000L // 14-may-2026
        }
    private lateinit var useCase: NewTreeUseCase

    @BeforeTest
    fun setup() {
        repository = FakeTreeRepository()
        useCase = NewTreeUseCase(repository, fakeDateProvider, dateFormatter)
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
                        birthDate = 0L,
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
                        birthDate = 0L,
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
                        birthDate = 631152000000L,
                        biologicalSex = Person.BiologicalSex.MALE,
                        sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                    ),
                )

            assertTrue(result.isSuccess)
            val tree = result.getOrNull()
            assertEquals("tree-1778716800000", tree?.id)
            assertEquals("Doe", tree?.name)
            assertEquals(0, tree?.ancestorCount)
            assertEquals("1778716800000", tree?.centralPerson?.id)
            assertEquals("John", tree?.centralPerson?.firstName)
            assertEquals(Person.BiologicalSex.MALE, tree?.centralPerson?.biologicalSex)
            assertEquals(Person.SexualOrientation.HETEROSEXUAL, tree?.centralPerson?.sexualOrientation)
            assertEquals("2026-05-14T00:00:00", tree?.lastUpdated)
        }
}

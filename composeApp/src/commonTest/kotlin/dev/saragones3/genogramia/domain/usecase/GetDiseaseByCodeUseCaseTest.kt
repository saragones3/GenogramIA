package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.fakes.FakeDiseaseRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetDiseaseByCodeUseCaseTest {
    private val repository = FakeDiseaseRepository()
    private val useCase = GetDiseaseByCodeUseCase(repository)

    private val disease =
        Disease(
            code = "BA00",
            title = "Hypertension",
            chapterCode = "11",
            chapterTitle = "Circulatory System",
            isGenetic = false,
        )

    @Test
    fun `GIVEN a code WHEN disease exists THEN returns the disease`() =
        runTest {
            repository.diseases = listOf(disease)

            val result = useCase("BA00")

            assertEquals(disease, result)
        }

    @Test
    fun `GIVEN a code WHEN disease does not exist THEN returns null`() =
        runTest {
            repository.diseases = listOf(disease)

            val result = useCase("INVALID")

            assertNull(result)
        }
}

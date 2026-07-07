package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.fakes.FakeDiseaseRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchDiseasesUseCaseTest {
    private val repository = FakeDiseaseRepository()
    private val useCase = SearchDiseasesUseCase(repository)

    @Test
    fun `GIVEN a query WHEN search THEN returns matching diseases`() =
        runTest {
            repository.diseases =
                listOf(
                    Disease(
                        code = "2A00.00",
                        title = "Glioblastoma de cerebro",
                        chapterCode = "02",
                        chapterTitle = "Neoplasias",
                        isGenetic = false,
                    ),
                    Disease(
                        code = "3A00.01",
                        title = "Anemia posthemorrágica crónica",
                        chapterCode = "03",
                        chapterTitle = "Enfermedades de la sangre o de los órganos hematopoyéticos",
                        isGenetic = false,
                    ),
                )

            val result = useCase("blastoma")

            assertEquals(1, result.size)
            assertEquals(
                Disease(
                    code = "2A00.00",
                    title = "Glioblastoma de cerebro",
                    chapterCode = "02",
                    chapterTitle = "Neoplasias",
                    isGenetic = false,
                ),
                result[0],
            )
        }
}

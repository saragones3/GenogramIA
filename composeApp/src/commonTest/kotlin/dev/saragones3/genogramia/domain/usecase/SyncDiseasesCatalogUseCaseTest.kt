package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.fakes.FakeDiseaseRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SyncDiseasesCatalogUseCaseTest {
    private val repository = FakeDiseaseRepository()
    private val useCase = SyncDiseasesCatalogUseCase(repository)

    @Test
    fun `GIVEN a catalog WHEN sync THEN triggers repository sync`() =
        runTest {
            useCase()

            assertTrue(repository.synced)
        }
}

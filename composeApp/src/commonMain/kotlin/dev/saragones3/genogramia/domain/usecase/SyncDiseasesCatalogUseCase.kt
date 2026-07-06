package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.repository.DiseaseRepository

class SyncDiseasesCatalogUseCase(
    private val repository: DiseaseRepository,
) {
    suspend operator fun invoke() = repository.syncCatalog()
}

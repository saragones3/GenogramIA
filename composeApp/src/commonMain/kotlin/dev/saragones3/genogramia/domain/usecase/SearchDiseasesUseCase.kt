package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.repository.DiseaseRepository

class SearchDiseasesUseCase(
    private val repository: DiseaseRepository,
) {
    suspend operator fun invoke(query: String): List<Disease> = repository.searchDiseases(query)
}

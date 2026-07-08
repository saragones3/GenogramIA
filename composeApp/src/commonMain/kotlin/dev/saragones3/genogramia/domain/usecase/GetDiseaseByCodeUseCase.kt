package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.repository.DiseaseRepository

class GetDiseaseByCodeUseCase(
    private val repository: DiseaseRepository,
) {
    suspend operator fun invoke(code: String): Disease? = repository.getDiseaseByCode(code)
}

package dev.saragones3.genogramia.fakes

import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.repository.DiseaseRepository

class FakeDiseaseRepository : DiseaseRepository {
    var diseases = emptyList<Disease>()
    var synced = false

    override suspend fun getDiseasesByChapter(chapterCode: String): List<Disease> =
        diseases.filter {
            it.chapterCode ==
                chapterCode
        }

    override suspend fun searchDiseases(query: String): List<Disease> = diseases.filter { it.title.contains(query) }

    override suspend fun getDiseaseByCode(code: String): Disease? = diseases.find { it.code == code }

    override suspend fun syncCatalog() {
        synced = true
    }
}

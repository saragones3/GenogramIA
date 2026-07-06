package dev.saragones3.genogramia.domain.repository

import dev.saragones3.genogramia.domain.model.Disease

interface DiseaseRepository {
    suspend fun getDiseasesByChapter(chapterCode: String): List<Disease>

    suspend fun searchDiseases(query: String): List<Disease>

    suspend fun syncCatalog()
}

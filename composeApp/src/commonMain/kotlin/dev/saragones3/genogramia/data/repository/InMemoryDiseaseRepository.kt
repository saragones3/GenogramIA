package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.data.remote.DiseasesRemoteDataSource
import dev.saragones3.genogramia.data.remote.toDomainList
import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.repository.DiseaseRepository

class InMemoryDiseaseRepository(
    private val remoteDataSource: DiseasesRemoteDataSource,
) : DiseaseRepository {
    private val cache = mutableMapOf<String, List<Disease>>()

    override suspend fun getDiseasesByChapter(chapterCode: String): List<Disease> = cache[chapterCode] ?: emptyList()

    override suspend fun searchDiseases(query: String): List<Disease> {
        val q = query.lowercase()
        return cache.values
            .flatten()
            .filter {
                it.title.lowercase().contains(q)
            }
    }

    override suspend fun getDiseaseByCode(code: String): Disease? = cache.values.flatten().find { it.code == code }

    override suspend fun syncCatalog() {
        remoteDataSource.getChapters().forEach { chapterCode ->
            try {
                val chapterDto = remoteDataSource.getChapter(chapterCode)
                cache[chapterCode] = chapterDto.toDomainList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

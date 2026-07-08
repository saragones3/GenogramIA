package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.core.database.DiseaseDao
import dev.saragones3.genogramia.core.database.model.DiseaseEntity
import dev.saragones3.genogramia.data.remote.DiseasesRemoteDataSource
import dev.saragones3.genogramia.data.remote.toDomainList
import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.repository.DiseaseRepository
import dev.saragones3.genogramia.util.getAppLanguage

class RoomDiseaseRepository(
    private val remoteDataSource: DiseasesRemoteDataSource,
    private val dao: DiseaseDao,
) : DiseaseRepository {
    override suspend fun getDiseasesByChapter(chapterCode: String): List<Disease> =
        dao.getDiseasesByChapter(chapterCode).map {
            it.toDomain()
        }

    override suspend fun searchDiseases(query: String): List<Disease> = dao.searchDiseases(query).map { it.toDomain() }

    override suspend fun getDiseaseByCode(code: String): Disease? = dao.getDiseaseByCode(code)?.toDomain()

    override suspend fun syncCatalog() {
        val currentLanguage = getAppLanguage()
        remoteDataSource.getChapters().forEach { chapterCode ->
            try {
                val localSync = dao.getChapterSync(chapterCode)
                val chapterDto = remoteDataSource.getChapter(chapterCode)

                val localSyncDate = localSync?.lastSyncDate
                val localLanguage = localSync?.language
                val remoteSyncDate = chapterDto.lastSync

                if (remoteSyncDate != localSyncDate || currentLanguage != localLanguage) {
                    val diseases = chapterDto.toDomainList()
                    dao.replaceChapterData(
                        chapterCode = chapterCode,
                        lastSyncDate = remoteSyncDate,
                        language = currentLanguage,
                        diseases = diseases.map { it.toEntity() },
                    )
                }
            } catch (e: Exception) {
                // Ignore sync errors for specific chapters to allow partial syncs
                e.printStackTrace()
            }
        }
    }
}

fun DiseaseEntity.toDomain() =
    Disease(
        code = code,
        title = title,
        chapterCode = chapterCode,
        chapterTitle = chapterTitle,
        isGenetic = isGenetic,
    )

fun Disease.toEntity() =
    DiseaseEntity(
        code = code,
        title = title,
        chapterCode = chapterCode,
        chapterTitle = chapterTitle,
        isGenetic = isGenetic,
    )

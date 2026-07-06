package dev.saragones3.genogramia.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.saragones3.genogramia.core.database.model.ChapterSyncEntity
import dev.saragones3.genogramia.core.database.model.DiseaseEntity

@Dao
interface DiseaseDao {
    @Query("SELECT * FROM disease WHERE chapterCode = :chapterCode")
    suspend fun getDiseasesByChapter(chapterCode: String): List<DiseaseEntity>

    @Query(
        "SELECT * FROM disease WHERE title LIKE '%' || :query || '%'",
    )
    suspend fun searchDiseases(query: String): List<DiseaseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiseases(diseases: List<DiseaseEntity>)

    @Query("DELETE FROM disease WHERE chapterCode = :chapterCode")
    suspend fun deleteDiseasesByChapter(chapterCode: String)

    @Query("SELECT * FROM chapter_sync WHERE chapterCode = :chapterCode")
    suspend fun getChapterSync(chapterCode: String): ChapterSyncEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapterSync(syncEntity: ChapterSyncEntity)

    @Transaction
    suspend fun replaceChapterData(
        chapterCode: String,
        lastSyncDate: String,
        diseases: List<DiseaseEntity>,
    ) {
        deleteDiseasesByChapter(chapterCode)
        insertDiseases(diseases)
        insertChapterSync(ChapterSyncEntity(chapterCode, lastSyncDate))
    }
}

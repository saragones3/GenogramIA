package dev.saragones3.genogramia.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.saragones3.genogramia.core.database.model.ChapterSyncEntity
import dev.saragones3.genogramia.core.database.model.DiseaseEntity

@Database(
    entities = [
        DiseaseEntity::class,
        ChapterSyncEntity::class,
    ],
    version = 1,
)
abstract class GenogramiaDatabase : RoomDatabase() {
    abstract fun diseaseDao(): DiseaseDao
}

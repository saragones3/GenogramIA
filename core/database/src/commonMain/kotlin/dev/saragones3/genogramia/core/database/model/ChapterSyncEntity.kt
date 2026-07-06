package dev.saragones3.genogramia.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapter_sync")
data class ChapterSyncEntity(
    @PrimaryKey
    val chapterCode: String,
    val lastSyncDate: String,
)

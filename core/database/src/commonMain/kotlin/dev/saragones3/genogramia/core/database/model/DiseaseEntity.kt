package dev.saragones3.genogramia.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disease")
data class DiseaseEntity(
    @PrimaryKey
    val code: String,
    val title: String,
    val chapterCode: String,
    val chapterTitle: String,
    val isGenetic: Boolean,
)

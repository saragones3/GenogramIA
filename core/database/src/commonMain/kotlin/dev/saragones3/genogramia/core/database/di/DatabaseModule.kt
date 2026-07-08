package dev.saragones3.genogramia.core.database.di

import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import dev.saragones3.genogramia.core.database.DiseaseDao
import dev.saragones3.genogramia.core.database.GenogramiaDatabase
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DATABASE_NAME = "GenogramIA"

private val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE chapter_sync ADD language TEXT NOT NULL DEFAULT en")
        }
    }

val databaseModule =
    module {
        includes(platformModule)
        single<String>(named("database_name")) { DATABASE_NAME }
        single<GenogramiaDatabase> {
            val builder = get<RoomDatabase.Builder<GenogramiaDatabase>>()
            builder
                .addMigrations(MIGRATION_1_2)
                .build()
        }
        single<DiseaseDao> { get<GenogramiaDatabase>().diseaseDao() }
    }

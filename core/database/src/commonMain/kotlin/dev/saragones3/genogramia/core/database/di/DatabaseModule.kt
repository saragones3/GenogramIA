package dev.saragones3.genogramia.core.database.di

import androidx.room.RoomDatabase
import dev.saragones3.genogramia.core.database.DiseaseDao
import dev.saragones3.genogramia.core.database.GenogramiaDatabase
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DATABASE_NAME = "GenogramIA"

val databaseModule =
    module {
        includes(platformModule)
        single<String>(named("database_name")) { DATABASE_NAME }
        single<GenogramiaDatabase> {
            val builder = get<RoomDatabase.Builder<GenogramiaDatabase>>()
            builder.build()
        }
        single<DiseaseDao> { get<GenogramiaDatabase>().diseaseDao() }
    }

package dev.saragones3.genogramia.core.database.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import dev.saragones3.genogramia.core.database.GenogramiaDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule =
    module {
        single<RoomDatabase.Builder<GenogramiaDatabase>> {
            Room
                .databaseBuilder(
                    context = get(),
                    klass = GenogramiaDatabase::class.java,
                    name = get(named("database_name")),
                ).setDriver(AndroidSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
        }
    }

package dev.saragones3.genogramia.di

import dev.saragones3.genogramia.core.database.di.databaseModule
import dev.saragones3.genogramia.data.repository.RoomDiseaseRepository
import dev.saragones3.genogramia.domain.repository.DiseaseRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun localDataSourceModule(): Module =
    module {
        includes(databaseModule)
        single<DiseaseRepository> { RoomDiseaseRepository(get(), get()) }
    }

package dev.saragones3.genogramia.di

import dev.saragones3.genogramia.data.repository.InMemoryDiseaseRepository
import dev.saragones3.genogramia.domain.repository.DiseaseRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun localDataSourceModule(): Module =
    module {
        single<DiseaseRepository> { InMemoryDiseaseRepository(get()) }
    }

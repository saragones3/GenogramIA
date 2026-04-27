package dev.saragones3.genogramia.di

import dev.saragones3.genogramia.data.firebase.FirebaseProvider
import dev.saragones3.genogramia.data.firebase.FirebaseProviderImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformDataModule(): Module =
    module {
        single<FirebaseProvider> { FirebaseProviderImpl() }
    }

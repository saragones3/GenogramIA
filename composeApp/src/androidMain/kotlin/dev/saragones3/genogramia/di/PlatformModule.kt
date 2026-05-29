package dev.saragones3.genogramia.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.saragones3.genogramia.data.remote.FirebaseProvider
import dev.saragones3.genogramia.data.remote.FirebaseProviderImpl
import dev.saragones3.genogramia.data.remote.FirestoreProvider
import dev.saragones3.genogramia.data.remote.FirestoreProviderImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformDataModule(): Module =
    module {
        single { FirebaseAuth.getInstance() }
        single { FirebaseFirestore.getInstance() }
        single<FirebaseProvider> { FirebaseProviderImpl(get()) }
        single<FirestoreProvider> { FirestoreProviderImpl(get()) }
    }

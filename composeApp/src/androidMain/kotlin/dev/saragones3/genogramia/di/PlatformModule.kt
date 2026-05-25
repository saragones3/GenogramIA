package dev.saragones3.genogramia.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.saragones3.genogramia.data.firebase.FirebaseProvider
import dev.saragones3.genogramia.data.firebase.FirebaseProviderImpl
import dev.saragones3.genogramia.data.firebase.FirestoreProvider
import dev.saragones3.genogramia.data.firebase.FirestoreProviderImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformDataModule(): Module =
    module {
        single { FirebaseAuth.getInstance() }
        single { FirebaseFirestore.getInstance() }
        single<FirebaseProvider> { FirebaseProviderImpl(get()) }
        single<FirestoreProvider> { FirestoreProviderImpl(get()) }
    }

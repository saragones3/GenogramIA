package dev.saragones3.genogramia.di

import dev.saragones3.genogramia.data.remote.FirebaseAuthDelegate
import dev.saragones3.genogramia.data.remote.FirebaseProvider
import dev.saragones3.genogramia.data.remote.FirebaseProviderImpl
import dev.saragones3.genogramia.data.remote.FirestoreDelegate
import dev.saragones3.genogramia.data.remote.FirestoreProvider
import dev.saragones3.genogramia.data.remote.FirestoreProviderImpl
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS platform data module. FirebaseProviderImpl is registered dynamically
 * in [initKoin] once the Swift delegate is available, so this module is intentionally empty.
 */
actual fun platformDataModule(): Module = module {}

internal fun iosFirebaseModule(
    authDelegate: FirebaseAuthDelegate,
    firestoreDelegate: FirestoreDelegate,
): Module =
    module {
        single<FirebaseProvider> { FirebaseProviderImpl(authDelegate) }
        single<FirestoreProvider> { FirestoreProviderImpl(firestoreDelegate) }
        single<HttpClientEngine> {
            Darwin.create {
                configureRequest {
                    setAllowsCellularAccess(true)
                }
            }
        }
    }

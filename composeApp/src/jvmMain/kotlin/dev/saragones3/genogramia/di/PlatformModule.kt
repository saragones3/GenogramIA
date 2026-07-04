package dev.saragones3.genogramia.di

import dev.saragones3.genogramia.data.remote.FirebaseProvider
import dev.saragones3.genogramia.data.remote.FirebaseProviderImpl
import dev.saragones3.genogramia.data.remote.FirestoreProvider
import dev.saragones3.genogramia.data.remote.FirestoreProviderImpl
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

actual fun platformDataModule(): Module =
    module {
        single<FirebaseProvider> { FirebaseProviderImpl() }
        single<FirestoreProvider> { FirestoreProviderImpl() }
        single<HttpClientEngine> {
            OkHttp.create {
                val interceptor = HttpLoggingInterceptor()
                preconfigured =
                    OkHttpClient
                        .Builder()
                        .addInterceptor(interceptor)
                        .connectTimeout(2, TimeUnit.MINUTES)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .followRedirects(false)
                        .build()
            }
        }
    }

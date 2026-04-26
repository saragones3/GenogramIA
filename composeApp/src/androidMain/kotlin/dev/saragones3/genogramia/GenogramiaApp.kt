package dev.saragones3.genogramia

import android.app.Application
import dev.saragones3.genogramia.di.getSharedModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GenogramiaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GenogramiaApp)
            modules(getSharedModules())
        }
    }
}

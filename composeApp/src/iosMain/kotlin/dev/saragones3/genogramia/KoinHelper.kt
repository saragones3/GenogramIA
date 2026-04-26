package dev.saragones3.genogramia

import dev.saragones3.genogramia.di.getSharedModules
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(getSharedModules())
    }
}

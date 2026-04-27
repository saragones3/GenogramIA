package dev.saragones3.genogramia

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.saragones3.genogramia.di.getSharedModules
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(getSharedModules())
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "GenogramIA",
        ) {
            App()
        }
    }
}

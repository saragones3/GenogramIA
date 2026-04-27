package dev.saragones3.genogramia

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.saragones3.genogramia.di.getSharedModules
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(getSharedModules())
    }
    ComposeViewport {
        App()
    }
}

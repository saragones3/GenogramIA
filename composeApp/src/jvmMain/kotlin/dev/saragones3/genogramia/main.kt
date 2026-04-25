package dev.saragones3.genogramia

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "GenogramIA",
    ) {
        App()
    }
}
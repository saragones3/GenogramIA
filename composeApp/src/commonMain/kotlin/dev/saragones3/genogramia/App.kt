package dev.saragones3.genogramia

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import dev.saragones3.genogramia.presentation.navigation.AppNavGraph
import dev.saragones3.genogramia.ui.theme.GenogramiaTheme

@Composable
fun App() {
    GenogramiaTheme {
        AppNavGraph()
    }
}

@Composable
@Preview
private fun AppPreview() {
    App()
}

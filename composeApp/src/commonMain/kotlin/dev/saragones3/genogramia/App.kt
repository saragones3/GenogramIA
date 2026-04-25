package dev.saragones3.genogramia

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.saragones3.genogramia.presentation.navigation.AppNavGraph

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavGraph()
    }
}
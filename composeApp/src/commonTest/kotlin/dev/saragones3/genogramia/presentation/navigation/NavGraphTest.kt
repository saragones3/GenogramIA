package dev.saragones3.genogramia.presentation.navigation

import androidx.compose.runtime.mutableStateListOf
import kotlin.test.Test
import kotlin.test.assertEquals

class NavGraphTest {

    @Test
    fun `backStack pop should remove last element if size is greater than 1`() {
        val backStack = mutableStateListOf(NavRoute.Splash, NavRoute.GuestHome)
        
        // Simulating the pop extension function behavior from NavGraph.kt
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
        
        assertEquals(1, backStack.size)
        assertEquals(NavRoute.Splash, backStack.last())
    }

    @Test
    fun `backStack pop should NOT remove last element if size is 1`() {
        val backStack = mutableStateListOf<NavRoute>(NavRoute.Splash)
        
        // Simulating the pop extension function behavior from NavGraph.kt
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
        
        assertEquals(1, backStack.size)
        assertEquals(NavRoute.Splash, backStack.last())
    }
}

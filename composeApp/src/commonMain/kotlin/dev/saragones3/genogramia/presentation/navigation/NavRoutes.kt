package dev.saragones3.genogramia.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoute {
    @Serializable
    data object GuestHome : NavRoute
    
    @Serializable
    data object Login : NavRoute
    
    @Serializable
    data object AuthenticatedHome : NavRoute
    
    @Serializable
    data object Legends : NavRoute
    
    @Serializable
    data object Settings : NavRoute
    
    @Serializable
    data object Registration : NavRoute
}

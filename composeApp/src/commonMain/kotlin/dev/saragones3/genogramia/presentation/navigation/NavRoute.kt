package dev.saragones3.genogramia.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoute {
    @Serializable
    data object Splash : NavRoute

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
    data object ChangePassword : NavRoute

    @Serializable
    data object Registration : NavRoute

    @Serializable
    data class ForgotPassword(
        val email: String? = null,
    ) : NavRoute

    @Serializable
    data object NewTree : NavRoute

    @Serializable
    data class Tree(
        val treeId: String,
    ) : NavRoute
}

package dev.saragones3.genogramia.presentation.settings

import dev.saragones3.genogramia.domain.model.User

data class SettingsState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
)

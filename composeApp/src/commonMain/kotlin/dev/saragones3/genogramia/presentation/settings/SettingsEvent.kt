package dev.saragones3.genogramia.presentation.settings

sealed interface SettingsEvent {
    data object OnLogOutClicked : SettingsEvent

    data object OnDeleteAccountClicked : SettingsEvent

    data object OnLogoutConfirmed : SettingsEvent

    data object OnDeleteConfirmed : SettingsEvent

    data object OnDismissDialogs : SettingsEvent
}

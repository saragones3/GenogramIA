package dev.saragones3.genogramia.di

import dev.saragones3.genogramia.presentation.authenticatedhome.AuthenticatedHomeViewModel
import dev.saragones3.genogramia.presentation.guesthome.GuestHomeViewModel
import dev.saragones3.genogramia.presentation.legends.LegendsViewModel
import dev.saragones3.genogramia.presentation.login.LoginViewModel
import dev.saragones3.genogramia.presentation.registration.RegistrationViewModel
import dev.saragones3.genogramia.presentation.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val dataModule =
    module {
        // TODO: Add repository implementations, Firebase, Ktor
    }

val domainModule =
    module {
        // TODO: Add Use Cases
    }

val appModule =
    module {
        viewModelOf(::GuestHomeViewModel)
        viewModelOf(::AuthenticatedHomeViewModel)
        viewModelOf(::LegendsViewModel)
        viewModelOf(::LoginViewModel)
        viewModelOf(::RegistrationViewModel)
        viewModelOf(::SettingsViewModel)
    }

fun getSharedModules(): List<Module> =
    listOf(
        dataModule,
        domainModule,
        appModule,
    )

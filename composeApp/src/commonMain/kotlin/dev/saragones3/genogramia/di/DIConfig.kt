package dev.saragones3.genogramia.di

import dev.saragones3.genogramia.data.repository.AuthRepositoryImpl
import dev.saragones3.genogramia.domain.repository.AuthRepository
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.presentation.authenticatedhome.AuthenticatedHomeViewModel
import dev.saragones3.genogramia.presentation.guesthome.GuestHomeViewModel
import dev.saragones3.genogramia.presentation.legends.LegendsViewModel
import dev.saragones3.genogramia.presentation.login.LoginViewModel
import dev.saragones3.genogramia.presentation.registration.RegistrationViewModel
import dev.saragones3.genogramia.presentation.settings.SettingsViewModel
import dev.saragones3.genogramia.presentation.splash.SplashViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val dataModule =
    module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }
    }

private val domainModule =
    module {
        factoryOf(::CheckSessionUseCase)
    }

private val appModule =
    module {
        viewModelOf(::SplashViewModel)
        viewModelOf(::GuestHomeViewModel)
        viewModelOf(::AuthenticatedHomeViewModel)
        viewModelOf(::LegendsViewModel)
        viewModelOf(::LoginViewModel)
        viewModelOf(::RegistrationViewModel)
        viewModelOf(::SettingsViewModel)
    }

fun getSharedModules(): List<Module> =
    listOf(
        platformDataModule(),
        dataModule,
        domainModule,
        appModule,
    )

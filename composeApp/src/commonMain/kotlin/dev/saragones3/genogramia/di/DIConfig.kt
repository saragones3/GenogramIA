package dev.saragones3.genogramia.di

import dev.saragones3.genogramia.data.repository.AuthRepositoryImpl
import dev.saragones3.genogramia.data.repository.FirestoreTreeRepository
import dev.saragones3.genogramia.data.repository.InMemoryTreeRepository
import dev.saragones3.genogramia.data.util.RealDateProvider
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.AuthRepository
import dev.saragones3.genogramia.domain.repository.TreeRepository
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.DeleteAccountUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreesUseCase
import dev.saragones3.genogramia.domain.usecase.NewTreeUseCase
import dev.saragones3.genogramia.domain.usecase.SendPasswordResetEmailUseCase
import dev.saragones3.genogramia.domain.usecase.SignInUseCase
import dev.saragones3.genogramia.domain.usecase.SignOutUseCase
import dev.saragones3.genogramia.domain.usecase.SignUpUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePasswordUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider
import dev.saragones3.genogramia.presentation.authenticatedhome.AuthenticatedHomeViewModel
import dev.saragones3.genogramia.presentation.changepassword.ChangePasswordViewModel
import dev.saragones3.genogramia.presentation.forgotpassword.ForgotPasswordViewModel
import dev.saragones3.genogramia.presentation.guesthome.GuestHomeViewModel
import dev.saragones3.genogramia.presentation.legends.LegendsViewModel
import dev.saragones3.genogramia.presentation.login.LoginViewModel
import dev.saragones3.genogramia.presentation.newtree.NewTreeViewModel
import dev.saragones3.genogramia.presentation.registration.RegistrationViewModel
import dev.saragones3.genogramia.presentation.settings.SettingsViewModel
import dev.saragones3.genogramia.presentation.splash.SplashViewModel
import dev.saragones3.genogramia.presentation.tree.TreeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val dataModule =
    module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }

        // Concrete implementations as singletons to preserve state
        single { InMemoryTreeRepository() }
        single { FirestoreTreeRepository() }

        // Dynamic repository that delegates based on current session state
        // This ensures that even if a ViewModel is reused, it always uses the correct repo
        single<TreeRepository> {
            val scope = this
            object : TreeRepository {
                private val authRepository: AuthRepository = scope.get()
                private val guestRepo: InMemoryTreeRepository = scope.get()
                private val authRepo: FirestoreTreeRepository = scope.get()

                private val activeRepo: TreeRepository
                    get() = if (authRepository.getCurrentUser() != null) authRepo else guestRepo

                override suspend fun createTree(tree: GenogramTree): GenogramTree = activeRepo.createTree(tree)

                override suspend fun getTree(id: String): GenogramTree? = activeRepo.getTree(id)

                override suspend fun getTrees(): List<GenogramTree> = activeRepo.getTrees()
            }
        }

        single<DateProvider> { RealDateProvider() }
    }

private val domainModule =
    module {
        factoryOf(::CheckSessionUseCase)
        factoryOf(::SignUpUseCase)
        factoryOf(::SignInUseCase)
        factoryOf(::SignOutUseCase)
        factoryOf(::DeleteAccountUseCase)
        factoryOf(::UpdatePasswordUseCase)
        factoryOf(::SendPasswordResetEmailUseCase)
        factoryOf(::NewTreeUseCase)
        factoryOf(::GetTreesUseCase)
        factoryOf(::GetTreeUseCase)
        factoryOf(::DateFormatter)
    }

private val appModule =
    module {
        viewModelOf(::SplashViewModel)
        viewModelOf(::GuestHomeViewModel)
        viewModelOf(::AuthenticatedHomeViewModel)
        viewModelOf(::NewTreeViewModel)
        viewModelOf(::LegendsViewModel)
        viewModelOf(::LoginViewModel)
        viewModelOf(::RegistrationViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::ChangePasswordViewModel)
        viewModelOf(::ForgotPasswordViewModel)
        viewModelOf(::TreeViewModel)
    }

fun getSharedModules(): List<Module> =
    listOf(
        platformDataModule(),
        dataModule,
        domainModule,
        appModule,
    )

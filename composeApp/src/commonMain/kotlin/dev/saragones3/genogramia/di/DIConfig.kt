package dev.saragones3.genogramia.di

import dev.saragones3.genogramia.data.remote.DiseasesRemoteDataSource
import dev.saragones3.genogramia.data.repository.AuthRepositoryImpl
import dev.saragones3.genogramia.data.repository.FirestoreTreeRepository
import dev.saragones3.genogramia.data.repository.InMemoryTreeRepository
import dev.saragones3.genogramia.data.util.RealDateProvider
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.AuthRepository
import dev.saragones3.genogramia.domain.repository.TreeRepository
import dev.saragones3.genogramia.domain.usecase.AddPersonUseCase
import dev.saragones3.genogramia.domain.usecase.AddRelationshipUseCase
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.DeleteAccountUseCase
import dev.saragones3.genogramia.domain.usecase.DeletePersonUseCase
import dev.saragones3.genogramia.domain.usecase.DeleteRelationshipUseCase
import dev.saragones3.genogramia.domain.usecase.DeleteTreeUseCase
import dev.saragones3.genogramia.domain.usecase.GetDiseaseByCodeUseCase
import dev.saragones3.genogramia.domain.usecase.GetPersonUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreesUseCase
import dev.saragones3.genogramia.domain.usecase.NewTreeUseCase
import dev.saragones3.genogramia.domain.usecase.SearchDiseasesUseCase
import dev.saragones3.genogramia.domain.usecase.SendPasswordResetEmailUseCase
import dev.saragones3.genogramia.domain.usecase.SignInUseCase
import dev.saragones3.genogramia.domain.usecase.SignOutUseCase
import dev.saragones3.genogramia.domain.usecase.SignUpUseCase
import dev.saragones3.genogramia.domain.usecase.SyncDiseasesCatalogUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePasswordUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePersonUseCase
import dev.saragones3.genogramia.domain.usecase.UpdateTreeUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider
import dev.saragones3.genogramia.presentation.addperson.AddPersonViewModel
import dev.saragones3.genogramia.presentation.addrelationship.AddRelationshipViewModel
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
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private const val BASE_DISEASES_URL = "cie11-diseases.web.app"

private val dataModule =
    module {
        single<AuthRepository> { AuthRepositoryImpl(get()) }

        // Concrete implementations as singletons to preserve state
        single { InMemoryTreeRepository() }
        single { FirestoreTreeRepository(get(), get()) }

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

                override suspend fun updateTree(tree: GenogramTree): GenogramTree = activeRepo.updateTree(tree)

                override suspend fun deleteTree(id: String) = activeRepo.deleteTree(id)
            }
        }

        single<DateProvider> { RealDateProvider() }

        single<HttpClient> {
            val httpClientEngine = get<HttpClientEngine>()
            HttpClient(httpClientEngine) {
                engine {
                    httpClientEngine.config
                }
            }.config {
                install(ContentNegotiation) {
                    json(
                        json = Json { ignoreUnknownKeys = true },
                        contentType = ContentType.Application.Json,
                    )
                }
                install(DefaultRequest) {
                    url {
                        protocol = URLProtocol.HTTPS
                        host = BASE_DISEASES_URL
                    }
                }
            }
        }
        single { DiseasesRemoteDataSource(get()) }
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
        factoryOf(::AddPersonUseCase)
        factoryOf(::AddRelationshipUseCase)
        factoryOf(::DeleteRelationshipUseCase)
        factoryOf(::DeletePersonUseCase)
        factoryOf(::DeleteTreeUseCase)
        factoryOf(::UpdatePersonUseCase)
        factoryOf(::UpdateTreeUseCase)
        factoryOf(::GetPersonUseCase)
        factoryOf(::GetTreesUseCase)
        factoryOf(::GetTreeUseCase)
        factoryOf(::SearchDiseasesUseCase)
        factoryOf(::GetDiseaseByCodeUseCase)
        factoryOf(::SyncDiseasesCatalogUseCase)
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
        viewModelOf(::AddPersonViewModel)
        viewModelOf(::AddRelationshipViewModel)
    }

fun getSharedModules(): List<Module> =
    listOf(
        localDataSourceModule(),
        platformDataModule(),
        dataModule,
        domainModule,
        appModule,
    )

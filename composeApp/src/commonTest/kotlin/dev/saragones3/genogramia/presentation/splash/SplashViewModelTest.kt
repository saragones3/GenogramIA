package dev.saragones3.genogramia.presentation.splash

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.SyncDiseasesCatalogUseCase
import dev.saragones3.genogramia.fakes.FakeAuthRepository
import dev.saragones3.genogramia.fakes.FakeDiseaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = FakeDiseaseRepository()
    private val syncUseCase = SyncDiseasesCatalogUseCase(repository)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN logged in user WHEN view model initialized THEN syncs catalog and navigates to authenticated home`() =
        runTest {
            val fakeRepo = FakeAuthRepository(User("uid", "email", "name"))
            val useCase = CheckSessionUseCase(fakeRepo)
            val viewModel = SplashViewModel(useCase, syncUseCase)

            viewModel.uiState.test {
                assertEquals(SplashUiState.NavigateToAuthenticatedHome, awaitItem())
                assertTrue(repository.synced)
            }
        }

    @Test
    fun `GIVEN no user WHEN view model initialized THEN syncs catalog and navigates to guest home`() =
        runTest {
            val fakeRepo = FakeAuthRepository(null)
            val useCase = CheckSessionUseCase(fakeRepo)
            val viewModel = SplashViewModel(useCase, syncUseCase)

            viewModel.uiState.test {
                assertEquals(SplashUiState.NavigateToGuestHome, awaitItem())
                assertTrue(repository.synced)
            }
        }
}

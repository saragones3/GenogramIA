package dev.saragones3.genogramia.presentation.newtree

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.NewTreeUseCase
import dev.saragones3.genogramia.domain.usecase.SearchDiseasesUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.fakes.FakeAuthRepository
import dev.saragones3.genogramia.fakes.FakeDateProvider
import dev.saragones3.genogramia.fakes.FakeDiseaseRepository
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class NewTreeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val treeRepository = FakeTreeRepository()
    private val authRepository = FakeAuthRepository()
    private val diseaseRepository = FakeDiseaseRepository()
    private val fakeDateProvider =
        FakeDateProvider().apply {
            currentTimeMillis = 1778716800000L // 14-may-2026
        }

    private val dateFormatter = DateFormatter()
    private val createTreeUseCase = NewTreeUseCase(treeRepository, fakeDateProvider, dateFormatter)
    private val checkSessionUseCase = CheckSessionUseCase(authRepository)
    private val searchDiseasesUseCase = SearchDiseasesUseCase(diseaseRepository)
    private lateinit var viewModel: NewTreeViewModel

    private val disease =
        Disease(
            code = "BA00",
            title = "Hypertension",
            chapterCode = "11",
            chapterTitle = "Circulatory System",
            isGenetic = false,
        )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository.setCurrentUser(null)
        viewModel =
            NewTreeViewModel(
                createTreeUseCase,
                checkSessionUseCase,
                searchDiseasesUseCase,
                dateFormatter,
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN view model WHEN initialized THEN initial state is correct`() =
        runTest {
            val state = viewModel.state.value
            assertEquals("", state.person.firstName)
            assertEquals("", state.person.lastName)
            assertEquals(Person.BiologicalSex.UNKNOWN, state.person.biologicalSex)
            assertEquals(Person.SexualOrientation.UNKNOWN, state.person.sexualOrientation)
            assertEquals("", state.person.birthDateText)
            assertEquals("", state.person.deathDateText)
            assertNull(state.firstNameError)
            assertNull(state.lastNameError)
            assertEquals(false, state.showBirthDatePicker)
            assertEquals(false, state.showDeathDatePicker)
            assertEquals(true, state.isGuest)
        }

    @Test
    fun `GIVEN logged in user WHEN view model initialized THEN isGuest is false`() =
        runTest {
            authRepository.setCurrentUser(User("uid", "email@test.com", "User"))
            val viewModel =
                NewTreeViewModel(
                    createTreeUseCase,
                    checkSessionUseCase,
                    searchDiseasesUseCase,
                    dateFormatter,
                )

            assertEquals(false, viewModel.state.value.isGuest)
        }

    @Test
    fun `GIVEN view model WHEN show birth date picker event received THEN visibility is updated`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnShowBirthDatePicker(true))
            assertEquals(true, viewModel.state.value.showBirthDatePicker)

            viewModel.onEvent(NewTreeEvent.OnShowBirthDatePicker(false))
            assertEquals(false, viewModel.state.value.showBirthDatePicker)
        }

    @Test
    fun `GIVEN view model WHEN show death date picker event received THEN visibility is updated`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnShowDeathDatePicker(true))
            assertEquals(true, viewModel.state.value.showDeathDatePicker)

            viewModel.onEvent(NewTreeEvent.OnShowDeathDatePicker(false))
            assertEquals(false, viewModel.state.value.showDeathDatePicker)
        }

    @Test
    fun `GIVEN view model WHEN birth date selected THEN state is updated with formatted date`() =
        runTest {
            val millis = 1778716800000L // 14-may-2026
            viewModel.onEvent(NewTreeEvent.OnBirthDateSelected(millis, "dd/MM/yyyy"))

            val state = viewModel.state.value
            assertEquals("14/05/2026", state.person.birthDateText)
            assertEquals(millis, state.person.birthDateMillis)
        }

    @Test
    fun `GIVEN view model WHEN death date selected THEN state is updated with formatted date`() =
        runTest {
            val millis = 1778716800000L // 14-may-2026
            viewModel.onEvent(NewTreeEvent.OnDeathDateSelected(millis, "dd/MM/yyyy"))

            val state = viewModel.state.value
            assertEquals("14/05/2026", state.person.deathDateText)
            assertEquals(millis, state.person.deathDateMillis)
        }

    @Test
    fun `GIVEN empty mandatory fields WHEN create tree clicked THEN validation fails`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnCreateTreeClicked)

            val state = viewModel.state.value
            assertEquals(NewTreeState.ValidationError.EMPTY, state.firstNameError)
            assertEquals(NewTreeState.ValidationError.EMPTY, state.lastNameError)
            assertEquals(NewTreeState.ValidationError.EMPTY, state.biologicalSexError)
            assertEquals(NewTreeState.ValidationError.EMPTY, state.sexualOrientationError)
        }

    @Test
    fun `GIVEN valid data WHEN create tree clicked THEN creation succeeds`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnFirstNameChanged("John"))
            viewModel.onEvent(NewTreeEvent.OnLastNameChanged("Doe"))
            viewModel.onEvent(NewTreeEvent.OnBirthDateSelected(1778716800000L, "dd/MM/yyyy")) // 14-may-2026
            viewModel.onEvent(NewTreeEvent.OnBiologicalSexChanged(Person.BiologicalSex.MALE))
            viewModel.onEvent(NewTreeEvent.OnSexualOrientationChanged(Person.SexualOrientation.HETEROSEXUAL))

            viewModel.state.test {
                assertEquals(
                    Person.SexualOrientation.HETEROSEXUAL,
                    awaitItem().person.sexualOrientation,
                ) // Last change emitted

                viewModel.onEvent(NewTreeEvent.OnCreateTreeClicked)

                // First state after click: loading
                assertEquals(true, awaitItem().isLoading)

                // Final state: success with navigation event
                val successState = awaitItem()
                assertEquals(false, successState.isLoading)
                assertEquals("tree-1778716800000", successState.navigationEvent)
            }
        }

    @Test
    fun `GIVEN modified state WHEN reset event received THEN state is restored to initial`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnFirstNameChanged("John"))
            viewModel.onEvent(NewTreeEvent.OnShowBirthDatePicker(true))
            assertEquals("John", viewModel.state.value.person.firstName)
            assertEquals(true, viewModel.state.value.showBirthDatePicker)
            assertEquals(true, viewModel.state.value.isGuest)

            viewModel.onEvent(NewTreeEvent.OnResetState)

            val state = viewModel.state.value
            assertEquals("", state.person.firstName)
            assertEquals(false, state.showBirthDatePicker)
            assertNull(state.firstNameError)
            assertEquals(true, state.isGuest)
        }

    @Test
    fun `GIVEN birth date set WHEN clear birth date event received THEN birth date is cleared`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnBirthDateSelected(1778716800000L, "dd/MM/yyyy"))
            assertEquals("14/05/2026", viewModel.state.value.person.birthDateText)

            viewModel.onEvent(NewTreeEvent.OnClearBirthDate)

            assertEquals("", viewModel.state.value.person.birthDateText)
            assertNull(viewModel.state.value.person.birthDateMillis)
        }

    @Test
    fun `GIVEN death date set WHEN clear death date event received THEN death date is cleared`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnDeathDateSelected(1778716800000L, "dd/MM/yyyy"))
            assertEquals("14/05/2026", viewModel.state.value.person.deathDateText)

            viewModel.onEvent(NewTreeEvent.OnClearDeathDate)

            assertEquals("", viewModel.state.value.person.deathDateText)
            assertNull(viewModel.state.value.person.deathDateMillis)
        }

    @Test
    fun `GIVEN disease search query WHEN changed THEN results are updated`() =
        runTest {
            diseaseRepository.diseases = listOf(disease)

            viewModel.onEvent(NewTreeEvent.OnDiseaseSearchQueryChanged("Hyp"))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, viewModel.state.value.diseaseSearchResults.size)
            assertEquals(
                "Hypertension",
                viewModel.state.value.diseaseSearchResults
                    .first()
                    .title,
            )
        }

    @Test
    fun `GIVEN disease selected WHEN added to history THEN medical history is updated`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnAddDiseaseToHistory(disease, 1778716800000L, "dd/MM/yyyy"))

            assertEquals(1, viewModel.state.value.person.medicalHistory.size)
            val condition =
                viewModel.state.value.person.medicalHistory
                    .first()
            assertEquals("BA00", condition.diseaseCode)
            assertEquals("14/05/2026", condition.diagnosisDateText)
        }

    @Test
    fun `GIVEN disease in history WHEN removed THEN medical history is updated`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnAddDiseaseToHistory(disease, null, "dd/MM/yyyy"))
            assertEquals(1, viewModel.state.value.person.medicalHistory.size)

            viewModel.onEvent(NewTreeEvent.OnRemoveDiseaseFromHistory("BA00"))

            assertEquals(0, viewModel.state.value.person.medicalHistory.size)
        }

    @Test
    fun `GIVEN add disease sheet shown WHEN dismissed THEN selection is reset`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnDiseaseSearchQueryChanged("Hyp"))
            viewModel.onEvent(NewTreeEvent.OnDiseaseSelected(disease))

            viewModel.onEvent(NewTreeEvent.OnShowAddDiseaseSheet(false))

            assertEquals("", viewModel.state.value.diseaseSearchQuery)
            assertNull(viewModel.state.value.selectedDisease)
        }
}

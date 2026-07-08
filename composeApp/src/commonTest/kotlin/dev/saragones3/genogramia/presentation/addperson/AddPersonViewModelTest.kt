package dev.saragones3.genogramia.presentation.addperson

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.AddPersonUseCase
import dev.saragones3.genogramia.domain.usecase.GetDiseaseByCodeUseCase
import dev.saragones3.genogramia.domain.usecase.GetPersonUseCase
import dev.saragones3.genogramia.domain.usecase.SearchDiseasesUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePersonUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
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
class AddPersonViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val treeRepository = FakeTreeRepository()
    private val diseaseRepository = FakeDiseaseRepository()

    private val fakeDateProvider =
        FakeDateProvider().apply {
            currentTimeMillis = 1778716800000L
        }

    private val dateFormatter = DateFormatter()
    private val addPersonUseCase = AddPersonUseCase(treeRepository, fakeDateProvider, dateFormatter)
    private val updatePersonUseCase = UpdatePersonUseCase(treeRepository, fakeDateProvider, dateFormatter)
    private val getPersonUseCase = GetPersonUseCase(treeRepository)
    private val searchDiseasesUseCase = SearchDiseasesUseCase(diseaseRepository)
    private val getDiseaseByCodeUseCase = GetDiseaseByCodeUseCase(diseaseRepository)
    private lateinit var viewModel: AddPersonViewModel

    private val tree =
        GenogramTree(
            id = "tree-1",
            ancestorCount = 1,
            lastUpdated = "2024-05-15",
            centralPerson =
                Person(
                    id = "p1",
                    firstName = "John",
                    lastName = "Doe",
                    birthDate = 1778716800000L,
                    biologicalSex = Person.BiologicalSex.MALE,
                    sexualOrientation = Person.SexualOrientation.HETEROSEXUAL,
                ),
        )

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
        viewModel =
            AddPersonViewModel(
                addPersonUseCase,
                updatePersonUseCase,
                getPersonUseCase,
                searchDiseasesUseCase,
                getDiseaseByCodeUseCase,
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
            assertNull(state.firstNameError)
            assertNull(state.lastNameError)
            assertEquals(false, state.isLoading)
            assertEquals(false, state.isSuccess)
        }

    @Test
    fun `GIVEN empty mandatory fields WHEN save clicked THEN validation fails`() =
        runTest {
            viewModel.onEvent(AddPersonEvent.OnSaveClicked("tree-1"))

            val state = viewModel.state.value
            assertEquals(AddPersonState.ValidationError.EMPTY, state.firstNameError)
            assertEquals(AddPersonState.ValidationError.EMPTY, state.lastNameError)
            assertEquals(AddPersonState.ValidationError.EMPTY, state.biologicalSexError)
            assertEquals(AddPersonState.ValidationError.EMPTY, state.sexualOrientationError)
        }

    @Test
    fun `GIVEN valid data WHEN save clicked THEN save succeeds`() =
        runTest {
            treeRepository.createTree(tree)

            viewModel.onEvent(AddPersonEvent.OnFirstNameChanged("Jane"))
            viewModel.onEvent(AddPersonEvent.OnLastNameChanged("Doe"))
            viewModel.onEvent(AddPersonEvent.OnBirthDateSelected(1778716800000L, "dd/MM/yyyy"))
            viewModel.onEvent(AddPersonEvent.OnBiologicalSexChanged(Person.BiologicalSex.FEMALE))
            viewModel.onEvent(AddPersonEvent.OnSexualOrientationChanged(Person.SexualOrientation.HETEROSEXUAL))

            viewModel.state.test {
                val initialState = awaitItem()
                assertEquals("Jane", initialState.person.firstName)
                assertEquals(1778716800000L, initialState.person.birthDateMillis)
                assertEquals("14/05/2026", initialState.person.birthDateText)

                viewModel.onEvent(AddPersonEvent.OnSaveClicked("tree-1"))

                assertEquals(true, awaitItem().isLoading)

                val successState = awaitItem()
                assertEquals(false, successState.isLoading)
                assertEquals(true, successState.isSuccess)
            }
        }

    @Test
    fun `GIVEN repository error WHEN save clicked THEN loading is hidden`() =
        runTest {
            treeRepository.shouldReturnError = true

            viewModel.onEvent(AddPersonEvent.OnFirstNameChanged("Jane"))
            viewModel.onEvent(AddPersonEvent.OnLastNameChanged("Doe"))
            viewModel.onEvent(AddPersonEvent.OnBirthDateSelected(1778716800000L, "dd/MM/yyyy"))
            viewModel.onEvent(AddPersonEvent.OnBiologicalSexChanged(Person.BiologicalSex.FEMALE))
            viewModel.onEvent(AddPersonEvent.OnSexualOrientationChanged(Person.SexualOrientation.HETEROSEXUAL))

            viewModel.state.test {
                assertEquals("Jane", awaitItem().person.firstName)

                viewModel.onEvent(AddPersonEvent.OnSaveClicked("tree-1"))

                assertEquals(true, awaitItem().isLoading)

                val errorState = awaitItem()
                assertEquals(false, errorState.isLoading)
                assertEquals(false, errorState.isSuccess)
            }
        }

    @Test
    fun `GIVEN person exists WHEN initialized with personId THEN data is loaded`() =
        runTest {
            treeRepository.createTree(tree)

            viewModel.state.test {
                awaitItem() // Initial

                viewModel.onEvent(AddPersonEvent.Initialize("tree-1", "p1", "dd/MM/yyyy"))

                assertEquals(true, awaitItem().isLoading)

                val loadedState = awaitItem()
                assertEquals(false, loadedState.isLoading)
                assertEquals("p1", loadedState.personId)
                assertEquals("John", loadedState.person.firstName)
                assertEquals(0f, loadedState.person.x)
                assertEquals(0f, loadedState.person.y)
            }
        }

    @Test
    fun `GIVEN new person WHEN initialized with coordinates THEN state is updated`() =
        runTest {
            viewModel.onEvent(AddPersonEvent.Initialize("tree-1", null, "dd/MM/yyyy", 150f, 250f))

            val state = viewModel.state.value
            assertNull(state.personId)
            assertEquals(150f, state.person.x)
            assertEquals(250f, state.person.y)
        }

    @Test
    fun `GIVEN edit mode WHEN save clicked THEN person is updated`() =
        runTest {
            treeRepository.createTree(tree)

            // Initialize to enter edit mode
            viewModel.onEvent(AddPersonEvent.Initialize("tree-1", "p1", "dd/MM/yyyy"))
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(AddPersonEvent.OnFirstNameChanged("John Updated"))

            viewModel.onEvent(AddPersonEvent.OnSaveClicked("tree-1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedTree = treeRepository.getTree("tree-1")
            assertEquals("John Updated", updatedTree?.centralPerson?.firstName)
            assertEquals(true, viewModel.state.value.isSuccess)
        }

    @Test
    fun `GIVEN previous edit WHEN initialized with null THEN state is reset`() =
        runTest {
            treeRepository.createTree(tree)

            // 1. Edit
            viewModel.onEvent(AddPersonEvent.Initialize("tree-1", "p1", "dd/MM/yyyy"))
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals("John", viewModel.state.value.person.firstName)

            // 2. New (null)
            viewModel.onEvent(AddPersonEvent.Initialize("tree-1", null, "dd/MM/yyyy"))

            // 3. Assert reset
            val state = viewModel.state.value
            assertEquals("", state.person.firstName)
            assertNull(state.personId)
            assertEquals(false, state.isSuccess)
        }

    @Test
    fun `GIVEN birth date set WHEN clear birth date event received THEN birth date is cleared`() =
        runTest {
            viewModel.onEvent(AddPersonEvent.OnBirthDateSelected(1778716800000L, "dd/MM/yyyy"))
            assertEquals("14/05/2026", viewModel.state.value.person.birthDateText)

            viewModel.onEvent(AddPersonEvent.OnClearBirthDate)

            assertEquals("", viewModel.state.value.person.birthDateText)
            assertNull(viewModel.state.value.person.birthDateMillis)
        }

    @Test
    fun `GIVEN death date set WHEN clear death date event received THEN death date is cleared`() =
        runTest {
            viewModel.onEvent(AddPersonEvent.OnDeathDateSelected(1778716800000L, "dd/MM/yyyy"))
            assertEquals("14/05/2026", viewModel.state.value.person.deathDateText)

            viewModel.onEvent(AddPersonEvent.OnClearDeathDate)

            assertEquals("", viewModel.state.value.person.deathDateText)
            assertNull(viewModel.state.value.person.deathDateMillis)
        }

    @Test
    fun `GIVEN disease search query WHEN changed THEN results are updated`() =
        runTest {
            diseaseRepository.diseases = listOf(disease)

            viewModel.onEvent(AddPersonEvent.OnDiseaseSearchQueryChanged("Hyp"))
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
            viewModel.onEvent(AddPersonEvent.OnAddDiseaseToHistory(disease, 1778716800000L, "dd/MM/yyyy"))

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
            viewModel.onEvent(AddPersonEvent.OnAddDiseaseToHistory(disease, null, "dd/MM/yyyy"))
            assertEquals(1, viewModel.state.value.person.medicalHistory.size)

            viewModel.onEvent(AddPersonEvent.OnRemoveDiseaseFromHistory("BA00"))

            assertEquals(0, viewModel.state.value.person.medicalHistory.size)
        }

    @Test
    fun `GIVEN add disease sheet shown WHEN dismissed THEN selection is reset`() =
        runTest {
            viewModel.onEvent(AddPersonEvent.OnDiseaseSearchQueryChanged("Hyp"))
            viewModel.onEvent(AddPersonEvent.OnDiseaseSelected(disease))

            viewModel.onEvent(AddPersonEvent.OnShowAddDiseaseSheet(false))

            assertEquals("", viewModel.state.value.diseaseSearchQuery)
            assertNull(viewModel.state.value.selectedDisease)
        }
}

package dev.saragones3.genogramia.presentation.addperson

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.AddPersonUseCase
import dev.saragones3.genogramia.domain.usecase.GetPersonUseCase
import dev.saragones3.genogramia.domain.usecase.UpdatePersonUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.fakes.FakeDateProvider
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

    private val fakeDateProvider =
        FakeDateProvider().apply {
            currentTimeMillis = 1778716800000L
        }

    private val dateFormatter = DateFormatter()
    private val addPersonUseCase = AddPersonUseCase(treeRepository, fakeDateProvider)
    private val updatePersonUseCase = UpdatePersonUseCase(treeRepository)
    private val getPersonUseCase = GetPersonUseCase(treeRepository)
    private lateinit var viewModel: AddPersonViewModel

    private val tree =
        GenogramTree(
            id = "tree-1",
            name = "Test Tree",
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

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddPersonViewModel(addPersonUseCase, updatePersonUseCase, getPersonUseCase, dateFormatter)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() =
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
    fun `when validation fails errors are updated`() =
        runTest {
            viewModel.onEvent(AddPersonEvent.OnSaveClicked("tree-1"))

            val state = viewModel.state.value
            assertEquals(AddPersonState.ValidationError.EMPTY, state.firstNameError)
            assertEquals(AddPersonState.ValidationError.EMPTY, state.lastNameError)
            assertEquals(AddPersonState.ValidationError.EMPTY, state.birthDateError)
            assertEquals(AddPersonState.ValidationError.EMPTY, state.biologicalSexError)
            assertEquals(AddPersonState.ValidationError.EMPTY, state.sexualOrientationError)
        }

    @Test
    fun `when data is valid save succeeds`() =
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
    fun `when save fails loading is hidden`() =
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
    fun `when initialize event is received with personId data is loaded`() =
        runTest {
            treeRepository.createTree(tree)

            viewModel.state.test {
                awaitItem() // Initial

                viewModel.onEvent(AddPersonEvent.Initialize("tree-1", "p1"))

                assertEquals(true, awaitItem().isLoading)

                val loadedState = awaitItem()
                assertEquals(false, loadedState.isLoading)
                assertEquals("p1", loadedState.personId)
                assertEquals("John", loadedState.person.firstName)
            }
        }

    @Test
    fun `when saving in edit mode UpdatePersonUseCase is used`() =
        runTest {
            treeRepository.createTree(tree)

            // Initialize to enter edit mode
            viewModel.onEvent(AddPersonEvent.Initialize("tree-1", "p1"))
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(AddPersonEvent.OnFirstNameChanged("John Updated"))

            viewModel.onEvent(AddPersonEvent.OnSaveClicked("tree-1"))
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedTree = treeRepository.getTree("tree-1")
            assertEquals("John Updated", updatedTree?.centralPerson?.firstName)
            assertEquals(true, viewModel.state.value.isSuccess)
        }

    @Test
    fun `when initializing with null after an edit state is reset`() =
        runTest {
            treeRepository.createTree(tree)

            // 1. Edit
            viewModel.onEvent(AddPersonEvent.Initialize("tree-1", "p1"))
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals("John", viewModel.state.value.person.firstName)

            // 2. New (null)
            viewModel.onEvent(AddPersonEvent.Initialize("tree-1", null))

            // 3. Assert reset
            val state = viewModel.state.value
            assertEquals("", state.person.firstName)
            assertNull(state.personId)
            assertEquals(false, state.isSuccess)
        }
}

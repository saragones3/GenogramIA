package dev.saragones3.genogramia.presentation.addperson

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.AddPersonUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.domain.util.DateProvider
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
        object : DateProvider {
            override fun nowEpochMilliseconds(): Long = 1778716800000L
        }

    private val dateFormatter = DateFormatter()
    private val addPersonUseCase = AddPersonUseCase(treeRepository, fakeDateProvider)
    private lateinit var viewModel: AddPersonViewModel

    private val tree =
        GenogramTree(
            id = "tree-1",
            name = "Test Tree",
            ancestorCount = 1,
            lastUpdated = "2024-05-15",
            centralPerson = Person(id = "p1", firstName = "John", lastName = "Doe"),
        )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddPersonViewModel(addPersonUseCase, dateFormatter)
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
                assertEquals("25/08/2026", initialState.person.birthDateText)

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
}

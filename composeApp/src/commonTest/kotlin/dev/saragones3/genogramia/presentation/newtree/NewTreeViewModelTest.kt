package dev.saragones3.genogramia.presentation.newtree

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.usecase.NewTreeUseCase
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
    private val repository = FakeTreeRepository()
    private val createTreeUseCase = NewTreeUseCase(repository)
    private lateinit var viewModel: NewTreeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NewTreeViewModel(createTreeUseCase)
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
            assertEquals(Person.BiologicalSex.UNKNOWN, state.person.biologicalSex)
            assertEquals(Person.SexualOrientation.UNKNOWN, state.person.sexualOrientation)
            assertNull(state.person.birthDate)
            assertNull(state.person.deathDate)
            assertNull(state.firstNameError)
            assertNull(state.lastNameError)
        }

    @Test
    fun `when mandatory fields are empty validation fails`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnCreateTreeClicked)

            val state = viewModel.state.value
            assertEquals(NewTreeState.ValidationError.EMPTY, state.firstNameError)
            assertEquals(NewTreeState.ValidationError.EMPTY, state.lastNameError)
            assertEquals(NewTreeState.ValidationError.EMPTY, state.birthDateError)
            assertEquals(NewTreeState.ValidationError.EMPTY, state.biologicalSexError)
            assertEquals(NewTreeState.ValidationError.EMPTY, state.sexualOrientationError)
        }

    @Test
    fun `when data is valid creation succeeds`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnFirstNameChanged("John"))
            viewModel.onEvent(NewTreeEvent.OnLastNameChanged("Doe"))
            viewModel.onEvent(NewTreeEvent.OnBirthDateChanged("01/01/1990"))
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
                assertEquals("tree-123456789", successState.navigationEvent)
            }
        }

    @Test
    fun `when reset event is received state is restored to initial`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnFirstNameChanged("John"))
            assertEquals("John", viewModel.state.value.person.firstName)

            viewModel.onEvent(NewTreeEvent.OnResetState)

            val state = viewModel.state.value
            assertEquals("", state.person.firstName)
            assertNull(state.firstNameError)
        }
}

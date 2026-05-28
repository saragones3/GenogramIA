package dev.saragones3.genogramia.presentation.newtree

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.usecase.CheckSessionUseCase
import dev.saragones3.genogramia.domain.usecase.NewTreeUseCase
import dev.saragones3.genogramia.domain.util.DateFormatter
import dev.saragones3.genogramia.fakes.FakeAuthRepository
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
class NewTreeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val treeRepository = FakeTreeRepository()
    private val authRepository = FakeAuthRepository()
    private val fakeDateProvider =
        FakeDateProvider().apply {
            currentTimeMillis = 1778716800000L // 14-may-2026
        }

    private val dateFormatter = DateFormatter()
    private val createTreeUseCase = NewTreeUseCase(treeRepository, fakeDateProvider, dateFormatter)
    private val checkSessionUseCase = CheckSessionUseCase(authRepository)
    private lateinit var viewModel: NewTreeViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository.setCurrentUser(null)
        viewModel = NewTreeViewModel(createTreeUseCase, checkSessionUseCase, dateFormatter)
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
            assertEquals("", state.person.birthDateText)
            assertEquals("", state.person.deathDateText)
            assertNull(state.firstNameError)
            assertNull(state.lastNameError)
            assertEquals(false, state.showBirthDatePicker)
            assertEquals(false, state.showDeathDatePicker)
            assertEquals(true, state.isGuest)
        }

    @Test
    fun `when user is logged in isGuest is false`() =
        runTest {
            authRepository.setCurrentUser(User("uid", "email@test.com", "User"))
            val viewModel = NewTreeViewModel(createTreeUseCase, checkSessionUseCase, dateFormatter)

            assertEquals(false, viewModel.state.value.isGuest)
        }

    @Test
    fun `when show birth date picker event is received visibility is updated`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnShowBirthDatePicker(true))
            assertEquals(true, viewModel.state.value.showBirthDatePicker)

            viewModel.onEvent(NewTreeEvent.OnShowBirthDatePicker(false))
            assertEquals(false, viewModel.state.value.showBirthDatePicker)
        }

    @Test
    fun `when show death date picker event is received visibility is updated`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnShowDeathDatePicker(true))
            assertEquals(true, viewModel.state.value.showDeathDatePicker)

            viewModel.onEvent(NewTreeEvent.OnShowDeathDatePicker(false))
            assertEquals(false, viewModel.state.value.showDeathDatePicker)
        }

    @Test
    fun `when birth date is selected state is updated with formatted date`() =
        runTest {
            val millis = 1778716800000L // 14-may-2026
            viewModel.onEvent(NewTreeEvent.OnBirthDateSelected(millis, "dd/MM/yyyy"))

            val state = viewModel.state.value
            assertEquals("14/05/2026", state.person.birthDateText)
            assertEquals(millis, state.person.birthDateMillis)
        }

    @Test
    fun `when death date is selected state is updated with formatted date`() =
        runTest {
            val millis = 1778716800000L // 14-may-2026
            viewModel.onEvent(NewTreeEvent.OnDeathDateSelected(millis, "dd/MM/yyyy"))

            val state = viewModel.state.value
            assertEquals("14/05/2026", state.person.deathDateText)
            assertEquals(millis, state.person.deathDateMillis)
        }

    @Test
    fun `when mandatory fields are empty validation fails`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnCreateTreeClicked)

            val state = viewModel.state.value
            assertEquals(NewTreeState.ValidationError.EMPTY, state.firstNameError)
            assertEquals(NewTreeState.ValidationError.EMPTY, state.lastNameError)
            assertEquals(NewTreeState.ValidationError.EMPTY, state.biologicalSexError)
            assertEquals(NewTreeState.ValidationError.EMPTY, state.sexualOrientationError)
        }

    @Test
    fun `when data is valid creation succeeds`() =
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
    fun `when reset event is received state is restored to initial`() =
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
    fun `when OnClearBirthDate event is received birth date is cleared`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnBirthDateSelected(1778716800000L, "dd/MM/yyyy"))
            assertEquals("14/05/2026", viewModel.state.value.person.birthDateText)

            viewModel.onEvent(NewTreeEvent.OnClearBirthDate)

            assertEquals("", viewModel.state.value.person.birthDateText)
            assertNull(viewModel.state.value.person.birthDateMillis)
        }

    @Test
    fun `when OnClearDeathDate event is received death date is cleared`() =
        runTest {
            viewModel.onEvent(NewTreeEvent.OnDeathDateSelected(1778716800000L, "dd/MM/yyyy"))
            assertEquals("14/05/2026", viewModel.state.value.person.deathDateText)

            viewModel.onEvent(NewTreeEvent.OnClearDeathDate)

            assertEquals("", viewModel.state.value.person.deathDateText)
            assertNull(viewModel.state.value.person.deathDateMillis)
        }
}

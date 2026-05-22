package dev.saragones3.genogramia.presentation.addrelationship

import app.cash.turbine.test
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.domain.model.Relationship
import dev.saragones3.genogramia.domain.usecase.AddRelationshipUseCase
import dev.saragones3.genogramia.domain.usecase.GetPersonUseCase
import dev.saragones3.genogramia.domain.usecase.GetTreeUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddRelationshipViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeTreeRepository()
    private val fakeDateProvider =
        object : DateProvider {
            override fun nowEpochMilliseconds(): Long = 1000L
        }
    private val dateFormatter = DateFormatter()
    private val getPersonUseCase = GetPersonUseCase(repository)
    private val getTreeUseCase = GetTreeUseCase(repository)
    private val addRelationshipUseCase = AddRelationshipUseCase(repository)
    private lateinit var viewModel: AddRelationshipViewModel

    private val person1 = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
    private val person2 = Person(id = "p2", firstName = "Jane", lastName = "Smith", birthDate = 0L)
    private val tree =
        GenogramTree(
            id = "tree-1",
            name = "Test Tree",
            ancestorCount = 2,
            lastUpdated = "2024-05-15",
            centralPerson = person1,
            persons = listOf(person2),
        )

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel =
            AddRelationshipViewModel(
                getPersonUseCase,
                getTreeUseCase,
                addRelationshipUseCase,
                fakeDateProvider,
                dateFormatter,
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() =
        runTest {
            val state = viewModel.state.value
            assertEquals(Relationship.RelationshipType.MARRIAGE, state.bondType)
            assertEquals(Relationship.EmotionalBond.POSITIVE, state.emotionalBond)
            assertFalse(state.isLoading)
            assertFalse(state.isSaving)
        }

    @Test
    fun `when init is called persons are loaded`() =
        runTest {
            repository.createTree(tree)

            viewModel.onResume("tree-1", "p1", "p2")
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(person1.id, state.person1?.id)
            assertEquals("${person1.firstName} ${person1.lastName}", state.person1?.fullName)
            assertEquals(person2.id, state.person2?.id)
            assertEquals("${person2.firstName} ${person2.lastName}", state.person2?.fullName)
            assertFalse(state.isLoading)
        }

    @Test
    fun `when consanguinity risk is detected hasConsanguinityRisk is true`() =
        runTest {
            val parent = Person(id = "p-parent", firstName = "Parent", lastName = "Root", birthDate = 0L)
            val p1 = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
            val p2 = Person(id = "p2", firstName = "Jane", lastName = "Smith", birthDate = 0L)

            val relationship1 =
                Relationship(
                    id = "rel-1",
                    personId1 = "p-parent",
                    personId2 = "p1",
                    type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                )
            val relationship2 =
                Relationship(
                    id = "rel-2",
                    personId1 = "p-parent",
                    personId2 = "p2",
                    type = Relationship.RelationshipType.BIOLOGICAL_OFFSPRING,
                )

            val consanguineousTree =
                GenogramTree(
                    id = "tree-consanguineous",
                    name = "Consanguineous Tree",
                    ancestorCount = 3,
                    lastUpdated = "2024-05-15",
                    centralPerson = parent,
                    persons = listOf(p1, p2),
                    relationships = listOf(relationship1, relationship2),
                )
            repository.createTree(consanguineousTree)

            viewModel.onResume("tree-consanguineous", "p1", "p2")
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(
                viewModel.state.value.hasConsanguinityRisk,
                "Should have consanguinity risk when sharing a parent",
            )
        }

    @Test
    fun `when no consanguinity risk is detected hasConsanguinityRisk is false`() =
        runTest {
            val p1 = Person(id = "p1", firstName = "John", lastName = "Doe", birthDate = 0L)
            val p2 = Person(id = "p2", firstName = "Jane", lastName = "Doe", birthDate = 0L)

            val treeNoRisk =
                GenogramTree(
                    id = "tree-no-risk",
                    name = "No Risk Tree",
                    ancestorCount = 2,
                    lastUpdated = "2024-05-15",
                    centralPerson = p1,
                    persons = listOf(p2),
                    relationships = emptyList(),
                )
            repository.createTree(treeNoRisk)

            viewModel.onResume("tree-no-risk", "p1", "p2")
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(
                viewModel.state.value.hasConsanguinityRisk,
                "Should NOT have consanguinity risk just by sharing last name",
            )
        }

    @Test
    fun `when bond type is selected state is updated`() =
        runTest {
            viewModel.onEvent(AddRelationshipEvent.OnBondTypeSelected(Relationship.RelationshipType.DIVORCE))
            assertEquals(Relationship.RelationshipType.DIVORCE, viewModel.state.value.bondType)
        }

    @Test
    fun `when emotional bond is selected state is updated`() =
        runTest {
            viewModel.onEvent(AddRelationshipEvent.OnEmotionalBondSelected(Relationship.EmotionalBond.CONFLICTUAL))
            assertEquals(Relationship.EmotionalBond.CONFLICTUAL, viewModel.state.value.emotionalBond)

            viewModel.onEvent(AddRelationshipEvent.OnEmotionalBondSelected(Relationship.EmotionalBond.POSITIVE))
            assertEquals(Relationship.EmotionalBond.POSITIVE, viewModel.state.value.emotionalBond)
        }

    @Test
    fun `when swap persons is triggered persons are exchanged in state`() =
        runTest {
            repository.createTree(tree)
            viewModel.onResume("tree-1", "p1", "p2")
            testDispatcher.scheduler.advanceUntilIdle()

            val initialState = viewModel.state.value
            assertEquals("John Doe", initialState.person1?.fullName)
            assertEquals("Jane Smith", initialState.person2?.fullName)

            viewModel.onEvent(AddRelationshipEvent.OnSwapPersons)

            val swappedState = viewModel.state.value
            assertEquals("Jane Smith", swappedState.person1?.fullName)
            assertEquals("John Doe", swappedState.person2?.fullName)
        }

    @Test
    fun `when confirm click is triggered relationship is saved`() =
        runTest {
            repository.createTree(tree)
            viewModel.onResume("tree-1", "p1", "p2")
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.state.test {
                awaitItem() // current state after init

                viewModel.onEvent(AddRelationshipEvent.OnConfirmClick)

                assertTrue(awaitItem().isSaving)
                val successState = awaitItem()
                assertFalse(successState.isSaving)
                assertTrue(successState.shouldNavigateBack)

                val updatedTree = repository.getTree("tree-1")
                assertEquals(1, updatedTree?.relationships?.size)
                assertEquals("p1", updatedTree?.relationships?.first()?.personId1)
                assertEquals("p2", updatedTree?.relationships?.first()?.personId2)
            }
        }

    @Test
    fun `when confirm click with emotional bond is triggered relationship is saved with bond`() =
        runTest {
            repository.createTree(tree)
            viewModel.onResume("tree-1", "p1", "p2")
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(AddRelationshipEvent.OnEmotionalBondSelected(Relationship.EmotionalBond.ABUSE))

            viewModel.onEvent(AddRelationshipEvent.OnConfirmClick)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedTree = repository.getTree("tree-1")
            val savedRelationship = updatedTree?.relationships?.first()
            assertEquals(Relationship.EmotionalBond.ABUSE, savedRelationship?.emotionalBond)
        }

    @Test
    fun `when onResume is called with relationshipId data is prefilled`() =
        runTest {
            val relId = "rel-123"
            val existingRel =
                Relationship(
                    id = relId,
                    personId1 = "p1",
                    personId2 = "p2",
                    type = Relationship.RelationshipType.COHABITATION,
                    emotionalBond = Relationship.EmotionalBond.CONFLICTUAL,
                    effectiveDate = 123456789L,
                )
            val treeWithRel = tree.copy(relationships = listOf(existingRel))
            repository.createTree(treeWithRel)

            viewModel.onResume("tree-1", null, null, relId)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals(relId, state.relationshipId)
            assertEquals(Relationship.RelationshipType.COHABITATION, state.bondType)
            assertEquals(Relationship.EmotionalBond.CONFLICTUAL, state.emotionalBond)
            assertEquals(123456789L, state.effectiveDate)
            assertEquals("p1", state.person1?.id)
            assertEquals("p2", state.person2?.id)
        }

    @Test
    fun `when saving an existing relationship it updates instead of adding new`() =
        runTest {
            val relId = "rel-123"
            val existingRel =
                Relationship(
                    id = relId,
                    personId1 = "p1",
                    personId2 = "p2",
                    type = Relationship.RelationshipType.MARRIAGE,
                    emotionalBond = Relationship.EmotionalBond.POSITIVE,
                )
            repository.createTree(tree.copy(relationships = listOf(existingRel)))

            viewModel.onResume("tree-1", null, null, relId)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onEvent(AddRelationshipEvent.OnBondTypeSelected(Relationship.RelationshipType.DIVORCE))
            viewModel.onEvent(AddRelationshipEvent.OnConfirmClick)
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedTree = repository.getTree("tree-1")
            assertEquals(1, updatedTree?.relationships?.size)
            assertEquals(Relationship.RelationshipType.DIVORCE, updatedTree?.relationships?.first()?.type)
            assertEquals(relId, updatedTree?.relationships?.first()?.id)
        }
}

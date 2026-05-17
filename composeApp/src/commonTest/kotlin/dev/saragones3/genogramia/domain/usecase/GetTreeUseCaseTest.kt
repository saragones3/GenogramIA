package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetTreeUseCaseTest {
    private lateinit var treeRepository: FakeTreeRepository
    private lateinit var getTreeUseCase: GetTreeUseCase

    @BeforeTest
    fun setup() {
        treeRepository = FakeTreeRepository()
        getTreeUseCase = GetTreeUseCase(treeRepository)
    }

    @Test
    fun `when tree exists it is returned`() =
        runTest {
            val person = Person("p1", "John", "Doe", 0L)
            val tree = GenogramTree("t1", "Family", 1, "now", person)
            treeRepository.createTree(tree)

            val result = getTreeUseCase("t1")

            assertEquals(tree, result)
        }

    @Test
    fun `when tree does not exist null is returned`() =
        runTest {
            val result = getTreeUseCase("non-existent")

            assertNull(result)
        }
}

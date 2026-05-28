package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.model.Person
import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTreesUseCaseTest {
    @Test
    fun `GIVEN trees in repository WHEN getting trees THEN returns all trees`() =
        runTest {
            val fakeRepository = FakeTreeRepository()
            val tree1 = GenogramTree("1", 1, "now", Person())
            val tree2 = GenogramTree("2", 2, "yesterday", Person())
            fakeRepository.createTree(tree1)
            fakeRepository.createTree(tree2)

            val useCase = GetTreesUseCase(fakeRepository)
            val result = useCase()

            assertEquals(2, result.size)
            assertEquals(tree1, result[0])
            assertEquals(tree2, result[1])
        }
}

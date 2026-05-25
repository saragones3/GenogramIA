package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.fakes.FakeTreeRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeleteTreeUseCaseTest {
    private lateinit var repository: FakeTreeRepository
    private lateinit var useCase: DeleteTreeUseCase

    @BeforeTest
    fun setUp() {
        repository = FakeTreeRepository()
        useCase = DeleteTreeUseCase(repository)
    }

    @Test
    fun `when invoking deleteTree then repository deleteTree is called`() =
        runTest {
            // Given
            val treeId = "1"
            repository.createTree(
                dev.saragones3.genogramia.domain.model.GenogramTree(
                    treeId,
                    "Test Tree",
                    0,
                    "",
                    dev.saragones3.genogramia.domain.model
                        .Person(),
                ),
            )
            assertNotNull(repository.getTree(treeId))

            // When
            useCase(treeId)

            // Then
            assertNull(repository.getTree(treeId))
        }
}

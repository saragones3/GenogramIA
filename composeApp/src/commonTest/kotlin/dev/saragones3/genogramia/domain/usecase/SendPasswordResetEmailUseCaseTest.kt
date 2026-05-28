package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SendPasswordResetEmailUseCaseTest {
    private lateinit var repository: FakeAuthRepository
    private lateinit var useCase: SendPasswordResetEmailUseCase

    @BeforeTest
    fun setup() {
        repository = FakeAuthRepository()
        useCase = SendPasswordResetEmailUseCase(repository)
    }

    @Test
    fun `GIVEN valid email WHEN send password reset called THEN returns success`() =
        runTest {
            val result = useCase("test@example.com")
            assertTrue(result.isSuccess)
        }

    @Test
    fun `GIVEN repository error WHEN send password reset called THEN returns failure`() =
        runTest {
            repository.shouldReturnError = true
            val result = useCase("test@example.com")
            assertTrue(result.isFailure)
        }
}

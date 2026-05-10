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
    fun `when repository is successful returns success result`() =
        runTest {
            val result = useCase("test@example.com")
            assertTrue(result.isSuccess)
        }

    @Test
    fun `when repository fails returns failure result`() =
        runTest {
            repository.shouldReturnError = true
            val result = useCase("test@example.com")
            assertTrue(result.isFailure)
        }
}

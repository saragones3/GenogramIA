package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignInUseCaseTest {
    private val repository = FakeAuthRepository()
    private val signInUseCase = SignInUseCase(repository)

    @Test
    fun `when signin is successful returns success with user`() =
        runTest {
            val email = "test@example.com"
            val password = "password123"

            val result = signInUseCase(email, password)

            assertTrue(result.isSuccess)
            val user = result.getOrNull()
            assertEquals(email, user?.email)
        }

    @Test
    fun `when signin fails returns failure`() =
        runTest {
            repository.shouldReturnError = true
            repository.errorToReturn = Exception("Fake signin error")
            val email = "test@example.com"
            val password = "password123"

            val result = signInUseCase(email, password)

            assertTrue(result.isFailure)
            assertEquals("Fake signin error", result.exceptionOrNull()?.message)
        }
}

package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignUpUseCaseTest {
    private val repository = FakeAuthRepository()
    private val signUpUseCase = SignUpUseCase(repository)

    @Test
    fun `when signup is successful returns success with user`() =
        runTest {
            val name = "Test User"
            val email = "test@example.com"
            val password = "password123"

            val result = signUpUseCase(name, email, password)

            assertTrue(result.isSuccess)
            val user = result.getOrNull()
            assertEquals(name, user?.displayName)
            assertEquals(email, user?.email)
        }

    @Test
    fun `when signup fails returns failure`() =
        runTest {
            repository.shouldReturnError = true
            val name = "Test User"
            val email = "test@example.com"
            val password = "password123"

            val result = signUpUseCase(name, email, password)

            assertTrue(result.isFailure)
            assertEquals("Fake signup error", result.exceptionOrNull()?.message)
        }
}

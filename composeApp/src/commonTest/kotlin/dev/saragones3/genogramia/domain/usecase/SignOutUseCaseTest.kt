package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNull

class SignOutUseCaseTest {
    private val repository = FakeAuthRepository()
    private val signOutUseCase = SignOutUseCase(repository)

    @Test
    fun `when signout is called then repository signout is called`() =
        runTest {
            // Set a user first
            repository.signInWithEmailAndPassword("test@test.com", "password")

            signOutUseCase()

            assertNull(repository.getCurrentUser())
        }
}

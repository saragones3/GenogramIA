package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNull

class DeleteAccountUseCaseTest {
    private val repository = FakeAuthRepository()
    private val deleteAccountUseCase = DeleteAccountUseCase(repository)

    @Test
    fun `GIVEN authenticated user WHEN delete account called THEN user is deleted`() =
        runTest {
            // Set a user first
            repository.signInWithEmailAndPassword("test@test.com", "password")

            deleteAccountUseCase()

            assertNull(repository.getCurrentUser())
        }
}

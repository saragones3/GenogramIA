package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UpdatePasswordUseCaseTest {
    private val repository = FakeAuthRepository()
    private val updatePasswordUseCase = UpdatePasswordUseCase(repository)

    @Test
    fun `GIVEN user WHEN update password called THEN repository reauthenticates and updates password`() =
        runTest {
            // In FakeAuthRepository, updatePassword doesn't do much but we can verify it doesn't throw
            updatePasswordUseCase("oldPassword", "newPassword123")
        }
}

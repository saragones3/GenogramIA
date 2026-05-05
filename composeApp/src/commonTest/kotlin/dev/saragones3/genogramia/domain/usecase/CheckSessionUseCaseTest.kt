package dev.saragones3.genogramia.domain.usecase

import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.repository.AuthRepository
import dev.saragones3.genogramia.fakes.FakeAuthRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CheckSessionUseCaseTest {
    @Test
    fun `invoke returns User when repository has a current user`() {
        val user = User("uid123", "test@test.com", "Test User")
        val fakeRepository = FakeAuthRepository(user)
        val useCase = CheckSessionUseCase(fakeRepository)

        val result = useCase()

        assertEquals(user, result)
    }

    @Test
    fun `invoke returns null when repository has no current user`() {
        val fakeRepository = FakeAuthRepository(null)
        val useCase = CheckSessionUseCase(fakeRepository)

        val result = useCase()

        assertNull(result)
    }
}

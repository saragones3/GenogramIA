package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.data.firebase.AuthUser
import dev.saragones3.genogramia.fakes.FakeFirebaseProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthRepositoryImplTest {
    @Test
    fun `GIVEN firebase user WHEN getting current user THEN returns mapped user`() {
        val authUser = AuthUser("uid456", "test@test.com", "John Doe")
        val fakeProvider = FakeFirebaseProvider(authUser)
        val repository = AuthRepositoryImpl(fakeProvider)

        val result = repository.getCurrentUser()

        assertEquals("uid456", result?.uid)
        assertEquals("test@test.com", result?.email)
        assertEquals("John Doe", result?.displayName)
    }

    @Test
    fun `GIVEN no firebase user WHEN getting current user THEN returns null`() {
        val fakeProvider = FakeFirebaseProvider(null)
        val repository = AuthRepositoryImpl(fakeProvider)

        val result = repository.getCurrentUser()

        assertNull(result)
    }
}

package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.data.firebase.AuthUser
import dev.saragones3.genogramia.fakes.FakeFirebaseProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthRepositoryImplTest {
    @Test
    fun `getCurrentUser returns mapped User when FirebaseProvider has user`() {
        val authUser = AuthUser("uid456", "test@test.com", "John Doe")
        val fakeProvider = FakeFirebaseProvider(authUser)
        val repository = AuthRepositoryImpl(fakeProvider)

        val result = repository.getCurrentUser()

        assertEquals("uid456", result?.uid)
        assertEquals("test@test.com", result?.email)
        assertEquals("John Doe", result?.displayName)
    }

    @Test
    fun `getCurrentUser returns null when FirebaseProvider has no user`() {
        val fakeProvider = FakeFirebaseProvider(null)
        val repository = AuthRepositoryImpl(fakeProvider)

        val result = repository.getCurrentUser()

        assertNull(result)
    }
}

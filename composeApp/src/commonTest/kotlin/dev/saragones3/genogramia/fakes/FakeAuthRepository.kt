package dev.saragones3.genogramia.fakes

import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.repository.AuthRepository

class FakeAuthRepository(
    private var currentUser: User? = null,
    var shouldReturnError: Boolean = false,
) : AuthRepository {
    override fun getCurrentUser(): User? = currentUser

    fun setCurrentUser(user: User?) {
        currentUser = user
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): User {
        if (shouldReturnError) {
            throw Exception("Fake login error")
        }
        val user = User("fake-id", email, "Fake Name")
        currentUser = user
        return user
    }

    override suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String,
    ): User {
        if (shouldReturnError) {
            throw Exception("Fake signup error")
        }
        val user = User("fake-id", email, name)
        currentUser = user
        return user
    }
}

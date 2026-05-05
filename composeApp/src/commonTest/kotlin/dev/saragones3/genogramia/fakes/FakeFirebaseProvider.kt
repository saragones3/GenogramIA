package dev.saragones3.genogramia.fakes

import dev.saragones3.genogramia.data.firebase.AuthUser
import dev.saragones3.genogramia.data.firebase.FirebaseProvider

class FakeFirebaseProvider(
    private var currentUser: AuthUser? = null,
) : FirebaseProvider {
    override fun getCurrentUser(): AuthUser? = currentUser

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser = throw NotImplementedError()

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser = throw NotImplementedError()

    override suspend fun sendPasswordResetEmail(email: String): Unit = throw NotImplementedError()

    override suspend fun updatePassword(newPassword: String): Unit = throw NotImplementedError()

    override suspend fun signOut() {
        currentUser = null
    }

    override suspend fun deleteCurrentUser() {
        currentUser = null
    }

    fun setCurrentUser(user: AuthUser?) {
        currentUser = user
    }
}

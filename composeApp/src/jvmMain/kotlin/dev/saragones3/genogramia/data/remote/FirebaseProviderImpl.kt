package dev.saragones3.genogramia.data.remote

import dev.saragones3.genogramia.data.remote.model.AuthUser
import kotlinx.coroutines.delay

/**
 * JVM implementation of FirebaseProvider.
 * Since there is no official Firebase SDK for Desktop/JVM that matches the mobile/web behavior,
 * this currently provides an in-memory mock implementation to allow the UI to function on Desktop.
 *
 * TODO: Replace with real Firebase REST integration if full Desktop support is required.
 */
internal class FirebaseProviderImpl : FirebaseProvider {
    private var currentUser: AuthUser? = null

    override fun getCurrentUser(): AuthUser? = currentUser

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser {
        delay(500) // Simulate network delay
        val user =
            AuthUser(
                uid = "jvm_mock_${email.hashCode()}",
                email = email,
                displayName = email.substringBefore("@"),
            )
        currentUser = user
        return user
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser {
        delay(500) // Simulate network delay
        val user =
            AuthUser(
                uid = "jvm_mock_${email.hashCode()}",
                email = email,
                displayName = email.substringBefore("@"),
            )
        currentUser = user
        return user
    }

    override suspend fun reauthenticate(password: String) {
        delay(300)
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        delay(500)
    }

    override suspend fun updatePassword(newPassword: String) {
        delay(500)
    }

    override suspend fun updateProfile(displayName: String?) {
        delay(500)
        currentUser = currentUser?.copy(displayName = displayName)
    }

    override suspend fun signOut() {
        delay(100)
        currentUser = null
    }

    override suspend fun deleteCurrentUser() {
        delay(100)
        currentUser = null
    }
}

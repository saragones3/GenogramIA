package dev.saragones3.genogramia.data.remote

import dev.saragones3.genogramia.data.remote.model.AuthUser

/**
 * Abstraction over Firebase Auth. Each platform provides its own implementation.
 */
interface FirebaseProvider {
    fun getCurrentUser(): AuthUser?

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser

    suspend fun reauthenticate(password: String)

    suspend fun sendPasswordResetEmail(email: String)

    suspend fun updatePassword(newPassword: String)

    suspend fun updateProfile(displayName: String?)

    suspend fun signOut()

    suspend fun deleteCurrentUser()
}

package dev.saragones3.genogramia.data.firebase

internal class FirebaseProviderImpl : FirebaseProvider {
    override fun getCurrentUser(): AuthUser? = null

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser = throw UnsupportedOperationException("Firebase Auth no está disponible en Desktop")

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser = throw UnsupportedOperationException("Firebase Auth no está disponible en Desktop")

    override suspend fun sendPasswordResetEmail(email: String): Unit =
        throw UnsupportedOperationException("Firebase Auth no está disponible en Desktop")

    override suspend fun updatePassword(newPassword: String): Unit =
        throw UnsupportedOperationException("Firebase Auth no está disponible en Desktop")

    override suspend fun signOut(): Unit =
        throw UnsupportedOperationException("Firebase Auth no está disponible en Desktop")

    override suspend fun deleteCurrentUser(): Unit =
        throw UnsupportedOperationException("Firebase Auth no está disponible en Desktop")
}

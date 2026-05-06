package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.data.firebase.FirebaseProvider
import dev.saragones3.genogramia.domain.model.User
import dev.saragones3.genogramia.domain.repository.AuthRepository

internal class AuthRepositoryImpl(
    private val firebaseProvider: FirebaseProvider,
) : AuthRepository {
    override fun getCurrentUser(): User? =
        firebaseProvider.getCurrentUser()?.let { User(it.uid, it.email, it.displayName) }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): User {
        val authUser = firebaseProvider.signInWithEmailAndPassword(email, password)
        return User(authUser.uid, authUser.email, authUser.displayName)
    }
}

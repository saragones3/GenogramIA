package dev.saragones3.genogramia.domain.repository

import dev.saragones3.genogramia.domain.model.User

interface AuthRepository {
    fun getCurrentUser(): User?

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): User
}

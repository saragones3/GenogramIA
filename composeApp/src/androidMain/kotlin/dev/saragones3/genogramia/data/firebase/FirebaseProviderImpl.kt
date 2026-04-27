package dev.saragones3.genogramia.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

internal class FirebaseProviderImpl : FirebaseProvider {
    private val auth = FirebaseAuth.getInstance()

    override fun getCurrentUser(): AuthUser? = auth.currentUser?.toAuthUser()

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user!!.toAuthUser()
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user!!.toAuthUser()
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun updatePassword(newPassword: String) {
        auth.currentUser!!.updatePassword(newPassword).await()
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun deleteCurrentUser() {
        auth.currentUser!!.delete().await()
    }
}

private fun FirebaseUser.toAuthUser() = AuthUser(uid, email, displayName)

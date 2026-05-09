package dev.saragones3.genogramia.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import dev.saragones3.genogramia.data.error.toAuthError
import kotlinx.coroutines.tasks.await

internal class FirebaseProviderImpl : FirebaseProvider {
    private val auth = FirebaseAuth.getInstance()

    override fun getCurrentUser(): AuthUser? = auth.currentUser?.toAuthUser()

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser =
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user!!.toAuthUser()
        } catch (e: Exception) {
            throw e.toAuthError()
        }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser =
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user!!.toAuthUser()
        } catch (e: Exception) {
            throw e.toAuthError()
        }

    override suspend fun sendPasswordResetEmail(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            throw e.toAuthError()
        }
    }

    override suspend fun updatePassword(newPassword: String) {
        try {
            auth.currentUser!!.updatePassword(newPassword).await()
        } catch (e: Exception) {
            throw e.toAuthError()
        }
    }

    override suspend fun updateProfile(displayName: String?) {
        try {
            val request =
                userProfileChangeRequest {
                    this.displayName = displayName
                }
            auth.currentUser!!.updateProfile(request).await()
        } catch (e: Exception) {
            throw e.toAuthError()
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun deleteCurrentUser() {
        try {
            auth.currentUser!!.delete().await()
        } catch (e: Exception) {
            throw e.toAuthError()
        }
    }
}

private fun FirebaseUser.toAuthUser() = AuthUser(uid, email, displayName)

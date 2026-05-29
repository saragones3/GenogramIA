package dev.saragones3.genogramia.data.remote

import dev.saragones3.genogramia.data.remote.model.AuthUser
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

internal class FirebaseProviderImpl(
    private val delegate: FirebaseAuthDelegate,
) : FirebaseProvider {
    override fun getCurrentUser(): AuthUser? {
        val uid = delegate.getCurrentUserUid() ?: return null
        return AuthUser(
            uid = uid,
            email = delegate.getCurrentUserEmail(),
            displayName = delegate.getCurrentUserDisplayName(),
        )
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser =
        suspendCancellableCoroutine { cont ->
            delegate.createUserWithEmail(
                email = email,
                password = password,
                onSuccess = { uid, mail, name -> cont.resumeWith(Result.success(AuthUser(uid, mail, name))) },
                onError = { msg -> cont.resumeWithException(Exception(msg).toAuthError()) },
            )
        }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser =
        suspendCancellableCoroutine { cont ->
            delegate.signInWithEmail(
                email = email,
                password = password,
                onSuccess = { uid, mail, name -> cont.resumeWith(Result.success(AuthUser(uid, mail, name))) },
                onError = { msg -> cont.resumeWithException(Exception(msg).toAuthError()) },
            )
        }

    override suspend fun reauthenticate(password: String): Unit =
        suspendCancellableCoroutine { cont ->
            delegate.reauthenticate(
                password = password,
                onSuccess = { cont.resumeWith(Result.success(Unit)) },
                onError = { msg -> cont.resumeWithException(Exception(msg).toAuthError()) },
            )
        }

    override suspend fun sendPasswordResetEmail(email: String): Unit =
        suspendCancellableCoroutine { cont ->
            delegate.sendPasswordResetEmail(
                email = email,
                onSuccess = { cont.resumeWith(Result.success(Unit)) },
                onError = { msg -> cont.resumeWithException(Exception(msg).toAuthError()) },
            )
        }

    override suspend fun updatePassword(newPassword: String): Unit =
        suspendCancellableCoroutine { cont ->
            delegate.updatePassword(
                newPassword = newPassword,
                onSuccess = { cont.resumeWith(Result.success(Unit)) },
                onError = { msg -> cont.resumeWithException(Exception(msg).toAuthError()) },
            )
        }

    override suspend fun updateProfile(displayName: String?): Unit =
        suspendCancellableCoroutine { cont ->
            delegate.updateProfile(
                displayName = displayName,
                onSuccess = { cont.resumeWith(Result.success(Unit)) },
                onError = { msg -> cont.resumeWithException(Exception(msg).toAuthError()) },
            )
        }

    override suspend fun signOut(): Unit =
        suspendCancellableCoroutine { cont ->
            delegate.signOut(
                onSuccess = { cont.resumeWith(Result.success(Unit)) },
                onError = { msg -> cont.resumeWithException(Exception(msg).toAuthError()) },
            )
        }

    override suspend fun deleteCurrentUser(): Unit =
        suspendCancellableCoroutine { cont ->
            delegate.deleteCurrentUser(
                onSuccess = { cont.resumeWith(Result.success(Unit)) },
                onError = { msg -> cont.resumeWithException(Exception(msg).toAuthError()) },
            )
        }
}

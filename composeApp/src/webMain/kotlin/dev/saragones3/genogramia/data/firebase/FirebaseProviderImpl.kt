@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package dev.saragones3.genogramia.data.firebase

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.JsAny
import kotlin.js.Promise

internal class FirebaseProviderImpl : FirebaseProvider {
    override fun getCurrentUser(): AuthUser? {
        val jsUser = getCurrentUserJs()
        return if (jsUser != null) {
            AuthUser(uid = jsUser.uid, email = jsUser.email, displayName = null)
        } else {
            null
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser {
        val result = createUserJs(getAuthJs(), email, password).await()
        return AuthUser(uid = result.user.uid, email = result.user.email, displayName = null)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AuthUser {
        val result = signInEmailJs(getAuthJs(), email, password).await()
        return AuthUser(uid = result.user.uid, email = result.user.email, displayName = null)
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        sendPasswordResetJs(getAuthJs(), email).await()
    }

    override suspend fun updatePassword(newPassword: String) {
        val user = getCurrentUserJs() ?: throw Exception("No authenticated user")
        updatePasswordJs(user, newPassword).await()
    }

    override suspend fun signOut() {
        signOutJs(getAuthJs()).await()
    }

    override suspend fun deleteCurrentUser() {
        val user = getCurrentUserJs() ?: throw Exception("No authenticated user")
        deleteUserJs(user).await()
    }
}

// Definiciones de tipos para WasmJS
external interface JsAuthUser : JsAny {
    val uid: String
    val email: String?
}

external interface JsAuthResult : JsAny {
    val user: JsAuthUser
}

// Interoperabilidad directa
@JsFun("() => window.firebaseAuth")
external fun getAuthJs(): JsAny

@JsFun("() => window.firebaseAuth.currentUser")
external fun getCurrentUserJs(): JsAuthUser?

@JsFun("(auth, email, pass) => window.firebaseAuthModule.createUserWithEmailAndPassword(auth, email, pass)")
external fun createUserJs(
    auth: JsAny,
    email: String,
    pass: String,
): Promise<JsAuthResult>

@JsFun("(auth, email, pass) => window.firebaseAuthModule.signInWithEmailAndPassword(auth, email, pass)")
external fun signInEmailJs(
    auth: JsAny,
    email: String,
    pass: String,
): Promise<JsAuthResult>

@JsFun("(auth, email) => window.firebaseAuthModule.sendPasswordResetEmail(auth, email)")
external fun sendPasswordResetJs(
    auth: JsAny,
    email: String,
): Promise<JsAny?>

@JsFun("(user, newPass) => window.firebaseAuthModule.updatePassword(user, newPass)")
external fun updatePasswordJs(
    user: JsAuthUser,
    newPass: String,
): Promise<JsAny?>

@JsFun("(auth) => window.firebaseAuthModule.signOut(auth)")
external fun signOutJs(auth: JsAny): Promise<JsAny?>

@JsFun("(user) => window.firebaseAuthModule.deleteUser(user)")
external fun deleteUserJs(user: JsAuthUser): Promise<JsAny?>

suspend fun <T : JsAny?> Promise<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        this.then(
            { result ->
                cont.resume(result)
                null // En WasmJS las lambdas de JS interop deben retornar JsAny?
            },
            { error ->
                cont.resumeWithException(RuntimeException(error.toString()))
                null
            },
        )
    }

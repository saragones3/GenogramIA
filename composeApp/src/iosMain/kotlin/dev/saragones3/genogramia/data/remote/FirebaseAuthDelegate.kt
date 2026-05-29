package dev.saragones3.genogramia.data.remote

/**
 * ObjC-compatible delegate interface implemented in Swift.
 * Bridges Kotlin/Native (iosMain) to the native Firebase iOS SDK added via SPM.
 *
 * All async operations use callbacks (onSuccess / onError) so that Swift can
 * implement this interface without needing to understand Kotlin coroutines.
 * FirebaseProviderImpl converts these callbacks into suspend functions internally.
 */
interface FirebaseAuthDelegate {
    fun getCurrentUserUid(): String?

    fun getCurrentUserEmail(): String?

    fun getCurrentUserDisplayName(): String?

    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: (uid: String, email: String?, displayName: String?) -> Unit,
        onError: (message: String) -> Unit,
    )

    fun reauthenticate(
        password: String,
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit,
    )

    fun createUserWithEmail(
        email: String,
        password: String,
        onSuccess: (uid: String, email: String?, displayName: String?) -> Unit,
        onError: (message: String) -> Unit,
    )

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit,
    )

    fun updatePassword(
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit,
    )

    fun updateProfile(
        displayName: String?,
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit,
    )

    fun signOut(
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit,
    )

    fun deleteCurrentUser(
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit,
    )
}

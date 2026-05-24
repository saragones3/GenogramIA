//
//  Untitled.swift
//  iosApp
//
//  Created by Sergio Aragonés on 27/4/26.
//
import ComposeApp
import FirebaseAuth

// MARK: - Swift implementation of the Kotlin FirebaseAuthDelegate protocol
// This class bridges the native Firebase iOS SDK to Kotlin/Native (iosMain).

class SwiftAuthDelegate: FirebaseAuthDelegate {
    
    private let auth = Auth.auth()

    func getCurrentUserUid() -> String? {
        auth.currentUser?.uid
    }

    func getCurrentUserEmail() -> String? {
        auth.currentUser?.email
    }

    func getCurrentUserDisplayName() -> String? {
        auth.currentUser?.displayName
    }

    func signInWithEmail(
        email: String,
        password: String,
        onSuccess: @escaping (String, String?, String?) -> Void,
        onError: @escaping (String) -> Void
    ) {
        auth.signIn(withEmail: email, password: password) { result, error in
            if let user = result?.user {
                onSuccess(user.uid, user.email, user.displayName)
            } else {
                onError(error?.localizedDescription ?? "Error desconocido")
            }
        }
    }
    
    func reauthenticate(
        password: String,
        onSuccess: @escaping () -> Void,
        onError: @escaping (String) -> Void
    ) {
        guard let user = auth.currentUser, let email = user.email else {
            onError("Error desconocido")
            return
        }
        let credential = EmailAuthProvider.credential(withEmail: email, password: password)
        user.reauthenticate(with: credential) { result, error in
            if let user = result?.user {
                onSuccess()
            } else {
                onError(error?.localizedDescription ?? "Error desconocido")
            }
        }
    }

    func createUserWithEmail(
        email: String,
        password: String,
        onSuccess: @escaping (String, String?, String?) -> Void,
        onError: @escaping (String) -> Void
    ) {
        auth.createUser(withEmail: email, password: password) { result, error in
            if let user = result?.user {
                onSuccess(user.uid, user.email, user.displayName)
            } else {
                onError(error?.localizedDescription ?? "Error desconocido")
            }
        }
    }

    func sendPasswordResetEmail(
        email: String,
        onSuccess: @escaping () -> Void,
        onError: @escaping (String) -> Void
    ) {
        auth.sendPasswordReset(withEmail: email) { error in
            if let error {
                onError(error.localizedDescription)
            } else {
                onSuccess()
            }
        }
    }

    func updatePassword(
        newPassword: String,
        onSuccess: @escaping () -> Void,
        onError: @escaping (String) -> Void
    ) {
        auth.currentUser?.updatePassword(to: newPassword) { error in
            if let error {
                onError(error.localizedDescription)
            } else {
                onSuccess()
            }
        }
    }
    
    func updateProfile(
        displayName: String?,
        onSuccess: @escaping () -> Void,
        onError: @escaping (String) -> Void
    ) {
        guard let changeRequest = auth.currentUser?.createProfileChangeRequest() else {
            onError("Error desconocido")
            return
        }
        changeRequest.displayName = displayName
        changeRequest.commitChanges { error in
            if let error {
                onError(error.localizedDescription)
            } else {
                onSuccess()
            }
        }
    }

    func signOut(
        onSuccess: @escaping () -> Void,
        onError: @escaping (String) -> Void
    ) {
        do {
            try auth.signOut()
            onSuccess()
        } catch {
            onError(error.localizedDescription)
        }
    }

    func deleteCurrentUser(
        onSuccess: @escaping () -> Void,
        onError: @escaping (String) -> Void
    ) {
        auth.currentUser?.delete { error in
            if let error {
                onError(error.localizedDescription)
            } else {
                onSuccess()
            }
        }
    }
}

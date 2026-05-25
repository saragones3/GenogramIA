//
//  SwiftFirestoreDelegate.swift
//  iosApp
//
//  Created by Sergio Aragonés on 25/05/2026.
//
import Foundation
import ComposeApp
import FirebaseFirestore

// MARK: - Swift implementation of the Kotlin FirestoreDelegate protocol
// This class bridges the native Firebase iOS SDK to Kotlin/Native (iosMain).

class SwiftFirestoreDelegate: FirestoreDelegate {
    
    private let db = Firestore.firestore()
    
    func saveTree(
        userId: String,
        treeJson: String,
        onSuccess: @escaping () -> Void,
        onError: @escaping (String) -> Void
    ) {
        guard let data = treeJson.data(using: .utf8),
              let jsonDict = try? JSONSerialization.jsonObject(with: data) as? [String: Any],
              let treeId = jsonDict["id"] as? String, !treeId.isEmpty else {
            onError("Invalid JSON or missing tree ID")
            return
        }
        
        db.collection("users")
            .document(userId)
            .collection("trees")
            .document(treeId)
            .setData(jsonDict) { error in
                if let error = error {
                    onError(error.localizedDescription)
                } else {
                    onSuccess()
                }
            }
    }
    
    func getTree(
        userId: String,
        treeId: String,
        onSuccess: @escaping (String?) -> Void,
        onError: @escaping (String) -> Void
    ) {
        db.collection("users")
            .document(userId)
            .collection("trees")
            .document(treeId)
            .getDocument { (snapshot, error) in
                if let error = error {
                    onError(error.localizedDescription)
                } else if let snapshot = snapshot, snapshot.exists, let data = snapshot.data() {
                    if let jsonData = try? JSONSerialization.data(withJSONObject: data),
                       let jsonString = String(data: jsonData, encoding: .utf8) {
                        onSuccess(jsonString)
                    } else {
                        onError("Error parsing document data")
                    }
                } else {
                    onSuccess(nil)
                }
            }
    }
    
    func getTrees(
        userId: String,
        onSuccess: @escaping ([String]) -> Void,
        onError: @escaping (String) -> Void
    ) {
        db.collection("users")
            .document(userId)
            .collection("trees")
            .getDocuments { (querySnapshot, error) in
                if let error = error {
                    onError(error.localizedDescription)
                } else {
                    let trees = querySnapshot?.documents.compactMap { document -> String? in
                        let data = document.data()
                        if let jsonData = try? JSONSerialization.data(withJSONObject: data),
                           let jsonString = String(data: jsonData, encoding: .utf8) {
                            return jsonString
                        }
                        return nil
                    } ?? []
                    onSuccess(trees)
                }
            }
    }
}

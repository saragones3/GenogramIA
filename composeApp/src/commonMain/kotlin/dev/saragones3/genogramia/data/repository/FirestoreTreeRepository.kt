package dev.saragones3.genogramia.data.repository

import dev.saragones3.genogramia.data.firebase.FirebaseProvider
import dev.saragones3.genogramia.data.firebase.FirestoreProvider
import dev.saragones3.genogramia.data.firebase.toDomain
import dev.saragones3.genogramia.data.firebase.toDto
import dev.saragones3.genogramia.domain.model.GenogramTree
import dev.saragones3.genogramia.domain.repository.TreeRepository

class FirestoreTreeRepository(
    private val firestoreProvider: FirestoreProvider,
    private val firebaseProvider: FirebaseProvider,
) : TreeRepository {
    override suspend fun createTree(tree: GenogramTree): GenogramTree {
        val uid = firebaseProvider.getCurrentUser()?.uid ?: return tree
        firestoreProvider.saveTree(uid, tree.toDto())
        return tree
    }

    override suspend fun getTree(id: String): GenogramTree? {
        val uid = firebaseProvider.getCurrentUser()?.uid ?: return null
        return firestoreProvider.getTree(uid, id)?.toDomain()
    }

    override suspend fun getTrees(): List<GenogramTree> {
        val uid = firebaseProvider.getCurrentUser()?.uid ?: return emptyList()
        return firestoreProvider.getTrees(uid).map { it.toDomain() }
    }

    override suspend fun updateTree(tree: GenogramTree): GenogramTree {
        val uid = firebaseProvider.getCurrentUser()?.uid ?: return tree
        firestoreProvider.saveTree(uid, tree.toDto())
        return tree
    }

    override suspend fun deleteTree(id: String) {
        val uid = firebaseProvider.getCurrentUser()?.uid ?: return
        firestoreProvider.deleteTree(uid, id)
    }
}

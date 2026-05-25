package dev.saragones3.genogramia.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import dev.saragones3.genogramia.data.firebase.dto.GenogramTreeDto
import kotlinx.coroutines.tasks.await

internal class FirestoreProviderImpl(
    private val db: FirebaseFirestore,
) : FirestoreProvider {
    override suspend fun saveTree(
        userId: String,
        tree: GenogramTreeDto,
    ) {
        db
            .collection("users")
            .document(userId)
            .collection("trees")
            .document(tree.id)
            .set(tree)
            .await()
    }

    override suspend fun getTree(
        userId: String,
        treeId: String,
    ): GenogramTreeDto? {
        val document =
            db
                .collection("users")
                .document(userId)
                .collection("trees")
                .document(treeId)
                .get()
                .await()

        return document.toObject(GenogramTreeDto::class.java)
    }

    override suspend fun getTrees(userId: String): List<GenogramTreeDto> {
        val snapshot =
            db
                .collection("users")
                .document(userId)
                .collection("trees")
                .get()
                .await()

        return snapshot.documents.mapNotNull { it.toObject(GenogramTreeDto::class.java) }
    }

    override suspend fun deleteTree(
        userId: String,
        treeId: String,
    ) {
        db
            .collection("users")
            .document(userId)
            .collection("trees")
            .document(treeId)
            .delete()
            .await()
    }
}

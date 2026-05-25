package dev.saragones3.genogramia.data.firebase

import dev.saragones3.genogramia.data.firebase.dto.GenogramTreeDto

interface FirestoreProvider {
    suspend fun saveTree(
        userId: String,
        tree: GenogramTreeDto,
    )

    suspend fun getTree(
        userId: String,
        treeId: String,
    ): GenogramTreeDto?

    suspend fun getTrees(userId: String): List<GenogramTreeDto>

    suspend fun deleteTree(
        userId: String,
        treeId: String,
    )
}

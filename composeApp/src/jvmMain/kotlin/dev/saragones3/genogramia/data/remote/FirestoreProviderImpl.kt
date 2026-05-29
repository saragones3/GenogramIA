package dev.saragones3.genogramia.data.remote

import dev.saragones3.genogramia.data.remote.model.GenogramTreeDto
import kotlinx.coroutines.delay

internal class FirestoreProviderImpl : FirestoreProvider {
    private val mockDb = mutableMapOf<String, MutableMap<String, GenogramTreeDto>>()

    override suspend fun saveTree(
        userId: String,
        tree: GenogramTreeDto,
    ) {
        delay(200)
        val userTrees = mockDb.getOrPut(userId) { mutableMapOf() }
        userTrees[tree.id] = tree
    }

    override suspend fun getTree(
        userId: String,
        treeId: String,
    ): GenogramTreeDto? {
        delay(200)
        return mockDb[userId]?.get(treeId)
    }

    override suspend fun getTrees(userId: String): List<GenogramTreeDto> {
        delay(200)
        return mockDb[userId]?.values?.toList() ?: emptyList()
    }

    override suspend fun deleteTree(
        userId: String,
        treeId: String,
    ) {
        delay(200)
        mockDb[userId]?.remove(treeId)
    }
}

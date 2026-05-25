package dev.saragones3.genogramia.fakes

import dev.saragones3.genogramia.data.firebase.FirestoreProvider
import dev.saragones3.genogramia.data.firebase.dto.GenogramTreeDto

class FakeFirestoreProvider : FirestoreProvider {
    val database = mutableMapOf<String, MutableMap<String, GenogramTreeDto>>()

    override suspend fun saveTree(
        userId: String,
        tree: GenogramTreeDto,
    ) {
        val userTrees = database.getOrPut(userId) { mutableMapOf() }
        userTrees[tree.id] = tree
    }

    override suspend fun getTree(
        userId: String,
        treeId: String,
    ): GenogramTreeDto? = database[userId]?.get(treeId)

    override suspend fun getTrees(userId: String): List<GenogramTreeDto> =
        database[userId]?.values?.toList() ?: emptyList()

    fun clear() {
        database.clear()
    }
}

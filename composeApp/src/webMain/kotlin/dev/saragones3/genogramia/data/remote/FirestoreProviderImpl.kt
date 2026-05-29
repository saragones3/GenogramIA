@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package dev.saragones3.genogramia.data.remote

import dev.saragones3.genogramia.data.remote.model.GenogramTreeDto
import kotlinx.serialization.json.Json
import kotlin.js.JsAny
import kotlin.js.Promise

internal class FirestoreProviderImpl : FirestoreProvider {
    override suspend fun saveTree(
        userId: String,
        tree: GenogramTreeDto,
    ) {
        val json = Json.encodeToString(GenogramTreeDto.serializer(), tree)
        saveTreeJs(getFirestoreJs(), userId, tree.id, json).await()
    }

    override suspend fun getTree(
        userId: String,
        treeId: String,
    ): GenogramTreeDto? {
        val json = getTreeJs(getFirestoreJs(), userId, treeId).await()?.toString()
        return json?.let { Json.decodeFromString(GenogramTreeDto.serializer(), it) }
    }

    override suspend fun getTrees(userId: String): List<GenogramTreeDto> {
        val jsonArrayStr = getTreesJs(getFirestoreJs(), userId).await()?.toString() ?: "[]"
        val jsonList = Json.decodeFromString<List<String>>(jsonArrayStr)
        return jsonList.map { Json.decodeFromString(GenogramTreeDto.serializer(), it) }
    }

    override suspend fun deleteTree(
        userId: String,
        treeId: String,
    ) {
        deleteTreeJs(getFirestoreJs(), userId, treeId).await()
    }
}

@JsFun("() => window.firebaseFirestore")
external fun getFirestoreJs(): JsAny

@JsFun("(db, userId, treeId, json) => window.firebaseFirestoreModule.saveTree(db, userId, treeId, json)")
external fun saveTreeJs(
    db: JsAny,
    userId: String,
    treeId: String,
    json: String,
): Promise<JsAny?>

@JsFun("(db, userId, treeId) => window.firebaseFirestoreModule.getTree(db, userId, treeId)")
external fun getTreeJs(
    db: JsAny,
    userId: String,
    treeId: String,
): Promise<JsAny?>

@JsFun("(db, userId) => window.firebaseFirestoreModule.getTrees(db, userId)")
external fun getTreesJs(
    db: JsAny,
    userId: String,
): Promise<JsAny?>

@JsFun("(db, userId, treeId) => window.firebaseFirestoreModule.deleteTree(db, userId, treeId)")
external fun deleteTreeJs(
    db: JsAny,
    userId: String,
    treeId: String,
): Promise<JsAny?>

package dev.saragones3.genogramia.data.firebase

import dev.saragones3.genogramia.data.firebase.dto.GenogramTreeDto
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class FirestoreProviderImpl(
    private val delegate: FirestoreDelegate,
) : FirestoreProvider {
    override suspend fun saveTree(
        userId: String,
        tree: GenogramTreeDto,
    ) {
        val json = Json.encodeToString(GenogramTreeDto.serializer(), tree)
        return suspendCancellableCoroutine { continuation ->
            delegate.saveTree(
                userId = userId,
                treeJson = json,
                onSuccess = { continuation.resume(Unit) },
                onError = { continuation.resumeWithException(Exception(it)) },
            )
        }
    }

    override suspend fun getTree(
        userId: String,
        treeId: String,
    ): GenogramTreeDto? =
        suspendCancellableCoroutine { continuation ->
            delegate.getTree(
                userId = userId,
                treeId = treeId,
                onSuccess = { json ->
                    val dto = json?.let { Json.decodeFromString(GenogramTreeDto.serializer(), it) }
                    continuation.resume(dto)
                },
                onError = { continuation.resumeWithException(Exception(it)) },
            )
        }

    override suspend fun getTrees(userId: String): List<GenogramTreeDto> =
        suspendCancellableCoroutine { continuation ->
            delegate.getTrees(
                userId = userId,
                onSuccess = { jsonList ->
                    val dtoList = jsonList.map { Json.decodeFromString(GenogramTreeDto.serializer(), it) }
                    continuation.resume(dtoList)
                },
                onError = { continuation.resumeWithException(Exception(it)) },
            )
        }
}

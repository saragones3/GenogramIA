package dev.saragones3.genogramia.data.remote

interface FirestoreDelegate {
    fun saveTree(
        userId: String,
        treeJson: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )

    fun getTree(
        userId: String,
        treeId: String,
        onSuccess: (String?) -> Unit,
        onError: (String) -> Unit,
    )

    fun getTrees(
        userId: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit,
    )

    fun deleteTree(
        userId: String,
        treeId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    )
}

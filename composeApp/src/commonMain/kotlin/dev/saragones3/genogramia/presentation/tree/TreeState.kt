package dev.saragones3.genogramia.presentation.tree

import androidx.compose.ui.geometry.Offset

data class TreeState(
    val tree: TreeUi = TreeUi(),
    val offset: Offset = Offset.Zero,
    val scale: Float = 1f,
    val isLoading: Boolean = false,
    val error: TreeError? = null,
    val shouldNavigateBack: Boolean = false,
    val selectedPersonIds: List<String> = emptyList(),
    val selectedRelationshipId: String? = null,
    val lastLoadedTreeId: String = "",
    val showDeleteConfirmation: Boolean = false,
    val personToDeleteName: String? = null,
)

enum class TreeError {
    NOT_FOUND,
    HAS_DESCENDANTS,
    HAS_FORMAL_RELATIONSHIPS,
    UNKNOWN,
}

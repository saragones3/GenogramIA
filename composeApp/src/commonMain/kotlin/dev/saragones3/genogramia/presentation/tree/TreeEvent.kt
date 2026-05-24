package dev.saragones3.genogramia.presentation.tree

import androidx.compose.ui.geometry.Offset

sealed interface TreeEvent {
    data class LoadTree(
        val id: String,
    ) : TreeEvent

    data class OnZoomIn(
        val delta: Float = 0.1f,
    ) : TreeEvent

    data class OnZoomOut(
        val delta: Float = 0.1f,
    ) : TreeEvent

    data object OnResetViewport : TreeEvent

    data class OnResetToCenter(
        val center: Offset,
    ) : TreeEvent

    data class OnPan(
        val offset: Offset,
    ) : TreeEvent

    data class OnTransform(
        val centroid: Offset,
        val pan: Offset,
        val zoom: Float,
    ) : TreeEvent

    data object OnNavigationConsumed : TreeEvent

    data object OnErrorConsumed : TreeEvent

    data class OnPersonSelected(
        val personId: String,
    ) : TreeEvent

    data object OnDismissSelection : TreeEvent

    data class OnPersonMove(
        val personId: String,
        val delta: Offset,
    ) : TreeEvent

    data class OnPersonMoveFinished(
        val personId: String,
    ) : TreeEvent

    data class OnViewportResetPerformed(
        val treeId: String,
    ) : TreeEvent

    data object OnDeleteSelectedPersonRequested : TreeEvent

    data object OnConfirmDeletePerson : TreeEvent

    data object OnDismissDeletePerson : TreeEvent
}

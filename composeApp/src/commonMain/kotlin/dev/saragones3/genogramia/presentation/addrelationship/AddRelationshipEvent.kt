package dev.saragones3.genogramia.presentation.addrelationship

import dev.saragones3.genogramia.domain.model.Relationship

sealed interface AddRelationshipEvent {
    data class OnBondTypeSelected(
        val type: Relationship.RelationshipType,
    ) : AddRelationshipEvent

    data class OnEmotionalBondSelected(
        val bond: Relationship.EmotionalBond,
    ) : AddRelationshipEvent

    data class OnDateSelected(
        val date: Long?,
        val pattern: String,
    ) : AddRelationshipEvent

    data object OnConfirmClick : AddRelationshipEvent

    data object OnDeleteClick : AddRelationshipEvent

    data object OnSwapPersons : AddRelationshipEvent

    data object OnBackClick : AddRelationshipEvent

    data object OnNavigationHandled : AddRelationshipEvent
}

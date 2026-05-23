package dev.saragones3.genogramia.presentation.addrelationship

import dev.saragones3.genogramia.domain.model.Relationship

data class AddRelationshipState(
    val person1: PersonUi? = null,
    val person2: PersonUi? = null,
    val bondType: Relationship.RelationshipType = Relationship.RelationshipType.MARRIAGE,
    val emotionalBond: Relationship.EmotionalBond = Relationship.EmotionalBond.POSITIVE,
    val effectiveDate: Long? = null,
    val effectiveDateFormatted: String? = null,
    val hasConsanguinityRisk: Boolean = false,
    val relationshipId: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val shouldNavigateBack: Boolean = false,
)

data class PersonUi(
    val id: String,
    val fullName: String,
    val isFemale: Boolean,
)

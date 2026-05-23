package dev.saragones3.genogramia.domain.model

data class Relationship(
    val id: String,
    val personId1: String,
    val personId2: String,
    val type: RelationshipType,
    val emotionalBond: EmotionalBond = EmotionalBond.POSITIVE,
    val effectiveDate: Long? = null,
) {
    enum class RelationshipType {
        // Structural
        MARRIAGE,
        COHABITATION,
        SEPARATION,
        DIVORCE,
        RECONCILIATION,

        // Vertical / Legal
        BIOLOGICAL_OFFSPRING,
        ADOPTION_LEGAL,
        ;

        val isStructural: Boolean
            get() = this in listOf(MARRIAGE, COHABITATION, SEPARATION, DIVORCE, RECONCILIATION)

        val isDescendant: Boolean
            get() = this in listOf(BIOLOGICAL_OFFSPRING, ADOPTION_LEGAL)
    }

    enum class EmotionalBond {
        POSITIVE,
        DISTANT,
        INTIMATE,
        INTIMATE_CONFLICTUAL,
        FOCUSED,
        FUSED,
        CONFLICTUAL,
        FUSED_CONFLICTUAL,
        DIRECT_CONFLICTUAL,
        HOSTILE,
        RUPTURE,
        ABUSE,
    }
}

package dev.saragones3.genogramia.presentation.newtree

import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.model.Person

sealed interface NewTreeEvent {
    data class OnFirstNameChanged(
        val firstName: String,
    ) : NewTreeEvent

    data class OnLastNameChanged(
        val lastName: String,
    ) : NewTreeEvent

    data class OnBiologicalSexChanged(
        val sex: Person.BiologicalSex,
    ) : NewTreeEvent

    data class OnSexualOrientationChanged(
        val orientation: Person.SexualOrientation,
    ) : NewTreeEvent

    data class OnSubstanceAbuseChanged(
        val abuse: Person.SubstanceAbuse,
    ) : NewTreeEvent

    data class OnMentalHealthProblemChanged(
        val hasProblem: Boolean,
    ) : NewTreeEvent

    data class OnBirthDateSelected(
        val millis: Long,
        val pattern: String,
    ) : NewTreeEvent

    data class OnDeathDateSelected(
        val millis: Long,
        val pattern: String,
    ) : NewTreeEvent

    data object OnClearBirthDate : NewTreeEvent

    data object OnClearDeathDate : NewTreeEvent

    data class OnShowBirthDatePicker(
        val show: Boolean,
    ) : NewTreeEvent

    data class OnShowDeathDatePicker(
        val show: Boolean,
    ) : NewTreeEvent

    data class OnShowAddDiseaseSheet(
        val show: Boolean,
    ) : NewTreeEvent

    data class OnShowDiagnosisDatePicker(
        val show: Boolean,
    ) : NewTreeEvent

    data class OnDiseaseSearchQueryChanged(
        val query: String,
    ) : NewTreeEvent

    data class OnDiseaseSelected(
        val disease: Disease?,
    ) : NewTreeEvent

    data class OnDiagnosisDateSelected(
        val millis: Long,
        val pattern: String,
    ) : NewTreeEvent

    data class OnAddDiseaseToHistory(
        val disease: Disease,
        val dateMillis: Long?,
        val datePattern: String,
    ) : NewTreeEvent

    data class OnRemoveDiseaseFromHistory(
        val diseaseCode: String,
    ) : NewTreeEvent

    data object OnCreateTreeClicked : NewTreeEvent

    data object OnNavigationConsumed : NewTreeEvent

    data object OnResetState : NewTreeEvent
}

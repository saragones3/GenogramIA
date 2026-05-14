package dev.saragones3.genogramia.presentation.newtree

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

    data class OnBirthDateSelected(
        val millis: Long,
        val pattern: String,
    ) : NewTreeEvent

    data class OnDeathDateSelected(
        val millis: Long,
        val pattern: String,
    ) : NewTreeEvent

    data class OnShowBirthDatePicker(
        val show: Boolean,
    ) : NewTreeEvent

    data class OnShowDeathDatePicker(
        val show: Boolean,
    ) : NewTreeEvent

    data object OnCreateTreeClicked : NewTreeEvent

    data object OnNavigationConsumed : NewTreeEvent

    data object OnResetState : NewTreeEvent
}

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

    data class OnBirthDateChanged(
        val date: String,
    ) : NewTreeEvent

    data class OnDeathDateChanged(
        val date: String,
    ) : NewTreeEvent

    data object OnCreateTreeClicked : NewTreeEvent

    data object OnNavigationConsumed : NewTreeEvent

    data object OnResetState : NewTreeEvent
}

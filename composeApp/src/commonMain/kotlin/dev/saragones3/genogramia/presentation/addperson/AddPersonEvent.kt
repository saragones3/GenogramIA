package dev.saragones3.genogramia.presentation.addperson

import dev.saragones3.genogramia.domain.model.Person

sealed interface AddPersonEvent {
    data class OnFirstNameChanged(
        val firstName: String,
    ) : AddPersonEvent

    data class OnLastNameChanged(
        val lastName: String,
    ) : AddPersonEvent

    data class OnBiologicalSexChanged(
        val sex: Person.BiologicalSex,
    ) : AddPersonEvent

    data class OnSexualOrientationChanged(
        val orientation: Person.SexualOrientation,
    ) : AddPersonEvent

    data class OnBirthDateSelected(
        val millis: Long,
        val pattern: String,
    ) : AddPersonEvent

    data class OnDeathDateSelected(
        val millis: Long,
        val pattern: String,
    ) : AddPersonEvent

    data class OnShowBirthDatePicker(
        val show: Boolean,
    ) : AddPersonEvent

    data class OnShowDeathDatePicker(
        val show: Boolean,
    ) : AddPersonEvent

    data class OnSaveClicked(
        val treeId: String,
    ) : AddPersonEvent

    data object OnResetState : AddPersonEvent
}

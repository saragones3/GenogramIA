package dev.saragones3.genogramia.presentation.newtree

import dev.saragones3.genogramia.domain.model.Person

data class NewTreeState(
    val person: NewTreeUi = NewTreeUi(),
    val firstNameError: ValidationError? = null,
    val lastNameError: ValidationError? = null,
    val birthDateError: ValidationError? = null,
    val biologicalSexError: ValidationError? = null,
    val sexualOrientationError: ValidationError? = null,
    val isLoading: Boolean = false,
    val isGuest: Boolean = false,
    val navigationEvent: String? = null,
    val showBirthDatePicker: Boolean = false,
    val showDeathDatePicker: Boolean = false,
) {
    enum class ValidationError {
        EMPTY,
    }
}

data class NewTreeUi(
    val firstName: String = "",
    val lastName: String = "",
    val birthDateMillis: Long = 0L,
    val birthDateText: String = "",
    val biologicalSex: Person.BiologicalSex = Person.BiologicalSex.UNKNOWN,
    val sexualOrientation: Person.SexualOrientation = Person.SexualOrientation.UNKNOWN,
    val deathDateMillis: Long? = null,
    val deathDateText: String = "",
)

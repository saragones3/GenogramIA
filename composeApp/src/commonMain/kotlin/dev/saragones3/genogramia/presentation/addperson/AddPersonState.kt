package dev.saragones3.genogramia.presentation.addperson

import dev.saragones3.genogramia.domain.model.Person

data class AddPersonState(
    val person: AddPersonUi = AddPersonUi(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val firstNameError: ValidationError? = null,
    val lastNameError: ValidationError? = null,
    val birthDateError: ValidationError? = null,
    val biologicalSexError: ValidationError? = null,
    val sexualOrientationError: ValidationError? = null,
    val showBirthDatePicker: Boolean = false,
    val showDeathDatePicker: Boolean = false,
) {
    enum class ValidationError {
        EMPTY,
    }
}

data class AddPersonUi(
    val firstName: String = "",
    val lastName: String = "",
    val biologicalSex: Person.BiologicalSex = Person.BiologicalSex.UNKNOWN,
    val sexualOrientation: Person.SexualOrientation = Person.SexualOrientation.UNKNOWN,
    val birthDateMillis: Long? = null,
    val deathDateMillis: Long? = null,
    val birthDateText: String = "",
    val deathDateText: String = "",
)

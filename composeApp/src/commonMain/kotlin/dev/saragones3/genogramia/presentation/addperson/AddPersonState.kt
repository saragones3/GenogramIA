package dev.saragones3.genogramia.presentation.addperson

import dev.saragones3.genogramia.domain.model.Person

data class AddPersonState(
    val person: Person = Person(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val firstNameError: ValidationError? = null,
    val lastNameError: ValidationError? = null,
    val birthDateError: ValidationError? = null,
    val biologicalSexError: ValidationError? = null,
    val sexualOrientationError: ValidationError? = null,
    val birthDateMillis: Long? = null,
    val deathDateMillis: Long? = null,
    val showBirthDatePicker: Boolean = false,
    val showDeathDatePicker: Boolean = false,
) {
    enum class ValidationError {
        EMPTY,
    }
}

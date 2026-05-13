package dev.saragones3.genogramia.presentation.newtree

import dev.saragones3.genogramia.domain.model.Person

data class NewTreeState(
    val person: Person = Person(),
    val firstNameError: ValidationError? = null,
    val lastNameError: ValidationError? = null,
    val birthDateError: ValidationError? = null,
    val biologicalSexError: ValidationError? = null,
    val sexualOrientationError: ValidationError? = null,
    val isLoading: Boolean = false,
    val navigationEvent: String? = null,
) {
    enum class ValidationError {
        EMPTY,
    }
}

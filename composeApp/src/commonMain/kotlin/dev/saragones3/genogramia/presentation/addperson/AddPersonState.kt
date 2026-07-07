package dev.saragones3.genogramia.presentation.addperson

import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.model.Person

data class AddPersonState(
    val person: AddPersonUi = AddPersonUi(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val firstNameError: ValidationError? = null,
    val lastNameError: ValidationError? = null,
    val biologicalSexError: ValidationError? = null,
    val sexualOrientationError: ValidationError? = null,
    val showBirthDatePicker: Boolean = false,
    val showDeathDatePicker: Boolean = false,
    val showAddDiseaseSheet: Boolean = false,
    val diseaseSearchQuery: String = "",
    val diseaseSearchResults: List<Disease> = emptyList(),
    val selectedDisease: Disease? = null,
    val diagnosisDateText: String = "",
    val personId: String? = null,
) {
    enum class ValidationError {
        EMPTY,
    }
}

data class AddPersonUi(
    val firstName: String = "",
    val lastName: String = "",
    val birthDateMillis: Long? = null,
    val birthDateText: String = "",
    val biologicalSex: Person.BiologicalSex = Person.BiologicalSex.UNKNOWN,
    val sexualOrientation: Person.SexualOrientation = Person.SexualOrientation.UNKNOWN,
    val deathDateMillis: Long? = null,
    val deathDateText: String = "",
    val x: Float = 0f,
    val y: Float = 0f,
)

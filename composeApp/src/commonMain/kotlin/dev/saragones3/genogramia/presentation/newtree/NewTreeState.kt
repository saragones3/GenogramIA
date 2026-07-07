package dev.saragones3.genogramia.presentation.newtree

import dev.saragones3.genogramia.domain.model.Disease
import dev.saragones3.genogramia.domain.model.Person

data class NewTreeState(
    val person: NewTreeUi = NewTreeUi(),
    val firstNameError: ValidationError? = null,
    val lastNameError: ValidationError? = null,
    val biologicalSexError: ValidationError? = null,
    val sexualOrientationError: ValidationError? = null,
    val isLoading: Boolean = false,
    val isGuest: Boolean = false,
    val navigationEvent: String? = null,
    val showBirthDatePicker: Boolean = false,
    val showDeathDatePicker: Boolean = false,
    val showAddDiseaseSheet: Boolean = false,
    val showDiagnosisDatePicker: Boolean = false,
    val diseaseSearchQuery: String = "",
    val diseaseSearchResults: List<Disease> = emptyList(),
    val selectedDisease: Disease? = null,
    val diagnosisDateMillis: Long? = null,
    val diagnosisDateText: String = "",
) {
    enum class ValidationError {
        EMPTY,
    }
}

data class NewTreeUi(
    val firstName: String = "",
    val lastName: String = "",
    val birthDateMillis: Long? = null,
    val birthDateText: String = "",
    val biologicalSex: Person.BiologicalSex = Person.BiologicalSex.UNKNOWN,
    val sexualOrientation: Person.SexualOrientation = Person.SexualOrientation.UNKNOWN,
    val deathDateMillis: Long? = null,
    val deathDateText: String = "",
    val medicalHistory: List<MedicalConditionUi> = emptyList(),
)

data class MedicalConditionUi(
    val diseaseCode: String,
    val diseaseTitle: String,
    val chapterCode: String,
    val chapterTitle: String,
    val isGenetic: Boolean,
    val diagnosisDateMillis: Long? = null,
    val diagnosisDateText: String = "",
)

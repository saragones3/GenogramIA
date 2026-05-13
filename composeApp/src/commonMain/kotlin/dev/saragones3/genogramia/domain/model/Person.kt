package dev.saragones3.genogramia.domain.model

data class Person(
    val id: String,
    val firstName: String,
    val lastName: String,
    val biologicalSex: BiologicalSex = BiologicalSex.UNKNOWN,
    val sexualOrientation: SexualOrientation = SexualOrientation.UNKNOWN,
    val birthDate: String? = null,
    val deathDate: String? = null,
) {
    constructor() : this(
        id = "",
        firstName = "",
        lastName = "",
    )

    enum class BiologicalSex {
        MALE,
        FEMALE,
        UNKNOWN,
    }

    enum class SexualOrientation {
        HETEROSEXUAL,
        OTHER,
        UNKNOWN,
    }
}

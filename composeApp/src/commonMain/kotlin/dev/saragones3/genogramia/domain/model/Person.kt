package dev.saragones3.genogramia.domain.model

data class Person(
    val id: String,
    val firstName: String,
    val lastName: String,
    val birthDate: Long? = null,
    val deathDate: Long? = null,
    val biologicalSex: BiologicalSex = BiologicalSex.UNKNOWN,
    val sexualOrientation: SexualOrientation = SexualOrientation.UNKNOWN,
    val x: Float = 0f,
    val y: Float = 0f,
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

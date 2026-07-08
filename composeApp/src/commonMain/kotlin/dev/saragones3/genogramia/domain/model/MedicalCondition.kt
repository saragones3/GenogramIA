package dev.saragones3.genogramia.domain.model

data class MedicalCondition(
    val diseaseCode: String,
    val diagnosisDate: Long? = null,
)

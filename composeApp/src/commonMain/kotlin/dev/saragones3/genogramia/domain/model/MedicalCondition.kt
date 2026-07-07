package dev.saragones3.genogramia.domain.model

data class MedicalCondition(
    val disease: Disease,
    val diagnosisDate: Long? = null,
)

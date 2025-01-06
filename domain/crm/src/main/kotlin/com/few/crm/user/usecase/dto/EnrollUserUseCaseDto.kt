package com.few.crm.user.usecase.dto

class EnrollUserUseCaseDto

data class EnrollUserUseCaseIn(
    val id: Long?,
    val externalId: String,
    val userAttributes: String,
)

data class EnrollUserUseCaseOut(
    val id: Long,
    val externalId: String,
    val userAttributes: String,
)
package com.few.crm.email.usecase.dto

class SendEmailUseCaseDto

data class SendEmailUseCaseIn(
    val templateId: String,
    val templateVersion: Float?,
    val userIds: List<String>,
)

class SendEmailUseCaseOut(
    val isSuccess: Boolean,
)
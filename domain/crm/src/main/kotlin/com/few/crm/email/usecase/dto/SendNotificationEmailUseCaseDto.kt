package com.few.crm.email.usecase.dto

class SendNotificationEmailUseCaseDto

data class SendNotificationEmailUseCaseIn(
    val templateId: Long,
    val templateVersion: Float?,
    val userIds: List<Long>,
)

class SendNotificationEmailUseCaseOut(
    val isSuccess: Boolean,
)
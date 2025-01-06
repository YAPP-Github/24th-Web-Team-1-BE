package com.few.crm.email.controller.request

data class SendNotificationEmailRequest(
    val templateId: Long,
    val templateVersion: Float?,
    val userIds: List<Long>?,
)
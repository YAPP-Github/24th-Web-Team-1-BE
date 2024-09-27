package com.few.api.domain.log.dto

import com.few.api.web.support.EmailLogEventType
import java.time.LocalDateTime

data class AddEmailLogUseCaseIn(
    val eventType: EmailLogEventType,
    val messageId: String,
    val destination: List<String>,
    val mailTimestamp: LocalDateTime,
)
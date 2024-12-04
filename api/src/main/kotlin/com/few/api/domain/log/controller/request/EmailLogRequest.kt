package com.few.api.domain.log.controller.request

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class EmailLogRequest(
    val eventType: String,
    val messageId: String,
    val destination: List<String>,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val mailTimestamp: LocalDateTime,
)
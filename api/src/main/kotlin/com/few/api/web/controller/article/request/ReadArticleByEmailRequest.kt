package com.few.api.web.controller.article.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.few.api.web.support.EmailLogEventType
import java.time.LocalDateTime

data class ReadArticleByEmailRequest(
    val eventType: EmailLogEventType,
    val messageId: String,
    val destination: List<String>,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val mailTimestamp: LocalDateTime,
)
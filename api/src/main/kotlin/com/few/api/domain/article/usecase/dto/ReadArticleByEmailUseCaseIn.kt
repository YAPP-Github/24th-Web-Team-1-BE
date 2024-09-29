package com.few.api.domain.article.usecase.dto

import com.few.api.web.support.EmailLogEventType
import com.few.api.web.support.SendType

data class ReadArticleByEmailUseCaseIn(
    val messageId: String,
    val destination: List<String>,
    val eventType: EmailLogEventType,
    val sendType: SendType,
)
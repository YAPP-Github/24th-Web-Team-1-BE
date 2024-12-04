package com.few.api.domain.article.usecase.dto

import com.few.api.domain.common.vo.EmailLogEventType
import com.few.api.domain.common.vo.SendType

data class ReadArticleByEmailUseCaseIn(
    val messageId: String,
    val destination: List<String>,
    val eventType: EmailLogEventType,
    val sendType: SendType,
)
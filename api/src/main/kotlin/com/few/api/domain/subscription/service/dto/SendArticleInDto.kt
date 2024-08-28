package com.few.api.domain.subscription.service.dto

import com.few.email.service.article.dto.Content

data class SendArticleInDto(
    val memberId: Long,
    val workbookId: Long,
    val toEmail: String,
    val articleDayCol: Byte,
    val articleTitle: String,
    val articleContent: Content,
)
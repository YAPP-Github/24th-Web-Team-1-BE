package com.few.api.domain.subscription.service.dto

import java.net.URL
import java.time.LocalDate

data class SendArticleInDto(
    val memberId: Long,
    val workbookId: Long,
    val toEmail: String,
    val articleDayCol: Byte,
    val articleTitle: String,
    val articleContent: ContentDto,
)

data class ContentDto(
    val memberEmail: String,
    val workbookId: Long,
    val articleId: Long,
    val currentDate: LocalDate,
    val category: String,
    val articleDay: Int,
    val articleTitle: String,
    val writerName: String,
    val writerLink: URL,
    val articleContent: String,
)
package com.few.api.domain.admin.document.service.dto

import java.net.URL
import java.time.LocalDateTime

data class InitializeArticleMainCardInDto(
    val articleId: Long,
    val articleTitle: String,
    val mainImageUrl: URL,
    val categoryCd: Byte,
    val createdAt: LocalDateTime,
    val writerId: Long,
    val writerEmail: String,
    val writerName: String,
    val writerImgUrl: URL,
    val writerUrl: URL,
)
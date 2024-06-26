package com.few.api.domain.article.dto

import java.net.URL
import java.time.LocalDateTime

data class ReadArticleUseCaseOut(
    val id: Long,
    val writer: WriterDetail,
    val title: String,
    val content: String,
    val problemIds: List<Long>,
    val category: String,
    val createdAt: LocalDateTime
)

data class WriterDetail(
    val id: Long,
    val name: String,
    val url: URL
)
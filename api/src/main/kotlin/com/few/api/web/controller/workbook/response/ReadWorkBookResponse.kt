package com.few.api.web.controller.workbook.response

import java.time.LocalDateTime

data class ReadWorkBookResponse(
    val id: Long,
    val name: Long,
    val mainImageUrl: String,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: LocalDateTime, // todo fix serialize
    val writerIds: List<Long>,
    val articles: List<ArticleInfo>
)

data class ArticleInfo(
    val id: Long,
    val title: String
)
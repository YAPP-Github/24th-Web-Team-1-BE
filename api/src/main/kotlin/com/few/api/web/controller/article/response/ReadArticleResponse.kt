package com.few.api.web.controller.article.response

import java.time.LocalDateTime

data class ReadArticleResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val problemIds: List<Long>,
    val category: String,
    val createdAt: LocalDateTime // todo fix serialize
)
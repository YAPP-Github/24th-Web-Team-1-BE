package com.few.api.web.controller.article.response

import java.net.URL
import java.time.LocalDateTime

data class ReadArticleResponse(
    val id: Long,
    val writer: WriterInfo,
    val title: String,
    val content: String,
    val problemIds: List<Long>,
    val category: String,
    val createdAt: LocalDateTime // todo fix serialize
)

data class WriterInfo(
    val id: Long,
    val name: String,
    val url: URL
)
package com.few.api.web.controller.article.response

import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseOut
import java.net.URL
import java.time.LocalDateTime

data class ReadArticleResponse(
    val id: Long,
    val writer: WriterInfo,
    val title: String,
    val content: String,
    val problemIds: List<Long>,
    val category: String,
    val createdAt: LocalDateTime,
) {
    constructor(
        useCaseOut: ReadArticleUseCaseOut,
    ) : this(
        id = useCaseOut.id,
        writer = WriterInfo(
            id = useCaseOut.writer.id,
            name = useCaseOut.writer.name,
            url = useCaseOut.writer.url
        ),
        title = useCaseOut.title,
        content = useCaseOut.content,
        problemIds = useCaseOut.problemIds,
        category = useCaseOut.category,
        createdAt = useCaseOut.createdAt
    )
}

data class WriterInfo(
    val id: Long,
    val name: String,
    val url: URL,
)
package com.few.api.web.controller.workbook.article.response

import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleOut
import com.few.api.web.controller.workbook.response.WriterInfo
import java.time.LocalDateTime

data class ReadWorkBookArticleResponse(
    val id: Long,
    val writer: WriterInfo,
    val title: String,
    val content: String,
    val problemIds: List<Long>,
    val category: String,
    val createdAt: LocalDateTime,
    val day: Long,
) {
    constructor(useCaseOut: ReadWorkBookArticleOut) : this(
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
        createdAt = useCaseOut.createdAt,
        day = useCaseOut.day
    )
}
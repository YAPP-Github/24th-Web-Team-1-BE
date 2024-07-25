package com.few.api.web.controller.workbook.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseOut
import java.net.URL
import java.time.LocalDateTime

data class BrowseWorkBooksResponse(
    val workbooks: List<BrowseWorkBooksInfo>,
)

data class BrowseWorkBooksInfo(
    val id: Long,
    val mainImageUrl: URL,
    val title: String,
    val description: String,
    val category: String,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val createdAt: LocalDateTime,
    val writers: List<WriterInfo>,
    val subscriberCount: Long = 0,
) {
    constructor(useCaseOut: ReadWorkbookUseCaseOut) :
        this(
            id = useCaseOut.id,
            mainImageUrl = useCaseOut.mainImageUrl,
            title = useCaseOut.title,
            description = useCaseOut.description,
            category = useCaseOut.category,
            createdAt = useCaseOut.createdAt,
            writers = useCaseOut.writers.map { WriterInfo(it.id, it.name, it.url) }
        )
}
package com.few.api.web.controller.workbook.response

import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseOut
import java.net.URL
import java.time.LocalDateTime

data class ReadWorkBookResponse(
    val id: Long,
    val mainImageUrl: URL,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: LocalDateTime,
    val writers: List<WriterInfo>,
    val articles: List<ArticleInfo>,
) {
    constructor(useCaseOut: ReadWorkbookUseCaseOut) :
        this(
            id = useCaseOut.id,
            mainImageUrl = useCaseOut.mainImageUrl,
            title = useCaseOut.title,
            description = useCaseOut.description,
            category = useCaseOut.category,
            createdAt = useCaseOut.createdAt,
            writers = useCaseOut.writers.map { WriterInfo(it.id, it.name, it.url) },
            articles = useCaseOut.articles.map { ArticleInfo(it.id, it.title) }
        )
}

data class ArticleInfo(
    val id: Long,
    val title: String,
)

/**
 * WorkBook 컨트롤러 패키지 내부에서 사용하는 작가 정보
 */
data class WriterInfo(
    val id: Long,
    val name: String,
    val url: URL,
)
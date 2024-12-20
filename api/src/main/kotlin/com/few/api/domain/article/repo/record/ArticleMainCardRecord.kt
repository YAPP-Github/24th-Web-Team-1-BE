package com.few.api.domain.article.repo.record

import java.net.URL
import java.time.LocalDateTime

data class ArticleMainCardRecord(
    val articleId: Long,
    val articleTitle: String,
    val mainImageUrl: URL,
    val categoryCd: Byte,
    val createdAt: LocalDateTime,
    val writerId: Long,
    val writerEmail: String,
    val writerName: String,
    val writerUrl: URL,
    val writerImgUrl: URL,
    val workbooks: List<WorkbookRecord> = emptyList(),
) {
    var content: String = ""
        set(value) {
            field = value
        }

    var views: Long = 0L
        set(value) {
            field = value
        }
}

data class WorkbookRecord(
    val id: Long?,
    val title: String?,
)
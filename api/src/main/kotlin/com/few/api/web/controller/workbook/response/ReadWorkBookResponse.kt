package com.few.api.web.controller.workbook.response

import java.net.URL
import java.time.LocalDateTime

data class ReadWorkBookResponse(
    val id: Long,
    val mainImageUrl: String,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: LocalDateTime, // todo fix serialize
    val writers: List<WriterInfo>,
    val articles: List<ArticleInfo>
)

data class ArticleInfo(
    val id: Long,
    val title: String
)

/**
 * WorkBook 컨트롤러 패키지 내부에서 사용하는 작가 정보
 */
data class WriterInfo(
    val id: Long,
    val name: String,
    val url: URL
)
package com.few.api.domain.article.repo.record

import java.net.URL

data class ArticleContentRecord(
    val id: Long,
    val category: String,
    val articleTitle: String,
    val articleContent: String,
    val writerName: String,
    val writerLink: URL,
)
package com.few.api.domain.subscription.service.dto

import java.net.URL

data class ReadArticleContentOutDto(
    val id: Long,
    val category: String,
    val articleTitle: String,
    val articleContent: String,
    val writerName: String,
    val writerLink: URL,
)
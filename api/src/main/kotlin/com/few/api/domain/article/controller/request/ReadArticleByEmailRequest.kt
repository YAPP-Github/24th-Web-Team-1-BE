package com.few.api.domain.article.controller.request

data class ReadArticleByEmailRequest(
    val messageId: String,
    val destination: List<String>,
)
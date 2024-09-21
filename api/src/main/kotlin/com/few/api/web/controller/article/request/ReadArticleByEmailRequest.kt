package com.few.api.web.controller.article.request

data class ReadArticleByEmailRequest(
    val messageId: String,
    val destination: List<String>,
)
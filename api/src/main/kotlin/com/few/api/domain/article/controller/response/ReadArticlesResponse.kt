package com.few.api.domain.article.controller.response

data class ReadArticlesResponse(
    val articles: List<ReadArticleResponse>,
    val isLast: Boolean,
)
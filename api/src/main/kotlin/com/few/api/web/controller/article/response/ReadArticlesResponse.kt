package com.few.api.web.controller.article.response

data class ReadArticlesResponse(
    val articles: List<ReadArticleResponse>,
    val isLast: Boolean,
)
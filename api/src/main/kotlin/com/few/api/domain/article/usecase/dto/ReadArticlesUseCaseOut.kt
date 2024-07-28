package com.few.api.domain.article.usecase.dto

data class ReadArticlesUseCaseOut(
    val articles: List<ReadArticleUseCaseOut>,
    val isLast: Boolean,
)
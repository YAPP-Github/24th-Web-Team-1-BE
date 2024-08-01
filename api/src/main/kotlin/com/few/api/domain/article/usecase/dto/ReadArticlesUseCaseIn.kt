package com.few.api.domain.article.usecase.dto

data class ReadArticlesUseCaseIn(
    val prevArticleId: Long,
    val categoryCd: Byte,
)
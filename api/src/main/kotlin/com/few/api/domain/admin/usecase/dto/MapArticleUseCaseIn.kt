package com.few.api.domain.admin.usecase.dto

data class MapArticleUseCaseIn(
    val workbookId: Long,
    val articleId: Long,
    val dayCol: Int,
)
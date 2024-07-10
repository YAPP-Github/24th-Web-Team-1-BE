package com.few.api.domain.admin.document.usecase.dto

data class MapArticleUseCaseIn(
    val workbookId: Long,
    val articleId: Long,
    val dayCol: Int
)
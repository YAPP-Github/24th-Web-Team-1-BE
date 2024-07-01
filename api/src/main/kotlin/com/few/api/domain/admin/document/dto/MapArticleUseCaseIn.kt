package com.few.api.domain.admin.document.dto

data class MapArticleUseCaseIn(
    val workbookId: Long,
    val articleId: Long,
    val dayCol: Int
)
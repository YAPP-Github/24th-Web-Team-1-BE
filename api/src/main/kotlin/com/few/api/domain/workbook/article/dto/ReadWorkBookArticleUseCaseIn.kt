package com.few.api.domain.workbook.article.dto

data class ReadWorkBookArticleUseCaseIn(
    val workbookId: Long,
    val articleId: Long,
)
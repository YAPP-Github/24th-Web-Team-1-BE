package com.few.api.domain.workbook.repo.command

data class MapWorkBookToArticleCommand(
    val workbookId: Long,
    val articleId: Long,
    val dayCol: Int,
)
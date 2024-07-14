package com.few.api.repo.dao.workbook.command

data class MapWorkBookToArticleCommand(
    val workbookId: Long,
    val articleId: Long,
    val dayCol: Int,
)
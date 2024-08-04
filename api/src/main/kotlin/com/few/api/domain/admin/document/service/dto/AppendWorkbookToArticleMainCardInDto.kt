package com.few.api.domain.admin.document.service.dto

data class AppendWorkbookToArticleMainCardInDto(
    val articleId: Long,
    val workbooks: List<WorkbookDto>,
)

data class WorkbookDto(
    val id: Long,
    val title: String,
)
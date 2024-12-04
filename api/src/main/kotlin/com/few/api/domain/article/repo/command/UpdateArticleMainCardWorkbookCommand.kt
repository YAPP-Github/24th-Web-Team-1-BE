package com.few.api.domain.article.repo.command

data class UpdateArticleMainCardWorkbookCommand(
    val articleId: Long,
    val workbooks: List<WorkbookCommand>,
)

data class WorkbookCommand(
    val id: Long,
    val title: String,
)
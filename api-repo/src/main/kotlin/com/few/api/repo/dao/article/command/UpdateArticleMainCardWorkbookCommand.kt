package com.few.api.repo.dao.article.command

data class UpdateArticleMainCardWorkbookCommand(
    val articleId: Long,
    val workbooks: List<WorkbookCommand>,
)

data class WorkbookCommand(
    val id: Long,
    val title: String,
)
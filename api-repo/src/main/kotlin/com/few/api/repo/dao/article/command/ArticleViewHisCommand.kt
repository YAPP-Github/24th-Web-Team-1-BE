package com.few.api.repo.dao.article.command

data class ArticleViewHisCommand(
    val articleId: Long,
    val memberId: Long,
)
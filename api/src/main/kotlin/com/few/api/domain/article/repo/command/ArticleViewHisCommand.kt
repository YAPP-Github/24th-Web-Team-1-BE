package com.few.api.domain.article.repo.command

data class ArticleViewHisCommand(
    val articleId: Long,
    val memberId: Long,
)
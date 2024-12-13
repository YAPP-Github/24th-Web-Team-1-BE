package com.few.api.domain.article.repo.query

import com.few.api.domain.common.vo.CategoryType

data class ArticleViewCountQuery(
    val articleId: Long,
    val categoryType: CategoryType,
)
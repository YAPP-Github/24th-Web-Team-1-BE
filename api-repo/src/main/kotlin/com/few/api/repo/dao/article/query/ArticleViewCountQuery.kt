package com.few.api.repo.dao.article.query

import com.few.data.common.code.CategoryType

data class ArticleViewCountQuery(
    val articleId: Long,
    val categoryType: CategoryType,
)
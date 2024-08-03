package com.few.api.repo.dao.article.query

import com.few.data.common.code.CategoryType

data class SelectArticlesOrderByViewsQuery(
    val offset: Long,
    val category: CategoryType,
)
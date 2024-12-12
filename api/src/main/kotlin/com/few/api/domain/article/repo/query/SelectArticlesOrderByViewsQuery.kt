package com.few.api.domain.article.repo.query

import com.few.api.domain.common.vo.CategoryType

data class SelectArticlesOrderByViewsQuery(
    val offset: Long,
    val category: CategoryType,
)
package com.few.api.domain.article.event.dto

import com.few.data.common.code.CategoryType

data class ReadArticleEvent(
    val articleId: Long,
    val memberId: Long,
    val category: CategoryType,
)
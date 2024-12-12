package com.few.api.domain.article.event.dto

import com.few.api.domain.common.vo.CategoryType

data class ReadArticleEvent(
    val articleId: Long,
    val memberId: Long,
    val category: CategoryType,
)
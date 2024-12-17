package com.few.api.domain.admin.controller.response

import com.few.api.domain.admin.usecase.dto.AddArticleUseCaseOut

data class AddArticleResponse(
    val articleId: Long,
) {
    constructor(useCaseOut: AddArticleUseCaseOut) : this(
        articleId = useCaseOut.articleId,
    )
}
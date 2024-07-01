package com.few.api.web.controller.admin.response

import com.few.api.domain.admin.document.dto.AddArticleUseCaseOut

data class AddArticleResponse(
    val articleId: Long
) {
    constructor(useCaseOut: AddArticleUseCaseOut) : this(
        articleId = useCaseOut.articleId
    )
}
package com.few.api.domain.subscription.usecase.dto

import com.few.api.web.support.ViewCategory

data class BrowseSubscribeWorkbooksUseCaseIn(
    val memberId: Long,
    val view: ViewCategory,
)
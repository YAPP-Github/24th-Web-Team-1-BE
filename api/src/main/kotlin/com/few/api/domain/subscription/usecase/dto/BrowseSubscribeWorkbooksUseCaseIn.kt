package com.few.api.domain.subscription.usecase.dto

import com.few.api.domain.common.vo.ViewCategory

data class BrowseSubscribeWorkbooksUseCaseIn(
    val memberId: Long,
    val view: ViewCategory,
)
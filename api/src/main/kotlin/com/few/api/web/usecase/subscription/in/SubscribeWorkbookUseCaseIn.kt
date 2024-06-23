package com.few.api.web.usecase.subscription.`in`

data class SubscribeWorkbookUseCaseIn(
    val workbookId: Long,
    val email: String,
    val memberId: Long
)
package com.few.api.domain.subscription.`in`

data class SubscribeWorkbookUseCaseIn(
    val workbookId: Long,
    val email: String,
    val memberId: Long
)
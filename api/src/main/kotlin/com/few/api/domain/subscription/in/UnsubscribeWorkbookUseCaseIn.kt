package com.few.api.domain.subscription.`in`

data class UnsubscribeWorkbookUseCaseIn(
    val workbookId: Long,
    val email: String,
    val opinion: String,
    val memberId: Long
)
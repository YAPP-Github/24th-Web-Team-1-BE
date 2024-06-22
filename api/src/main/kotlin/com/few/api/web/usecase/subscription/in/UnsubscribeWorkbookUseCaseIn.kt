package com.few.api.web.usecase.subscription.`in`

data class UnsubscribeWorkbookUseCaseIn(
    val workbookId: Long,
    val email: String,
    val opinion: String,
    val memberId: Long
)
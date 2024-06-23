package com.few.api.domain.subscription.usecase.`in`

data class UnsubscribeWorkbookUseCaseIn(
    val workbookId: Long,
    val email: String,
    val opinion: String
)
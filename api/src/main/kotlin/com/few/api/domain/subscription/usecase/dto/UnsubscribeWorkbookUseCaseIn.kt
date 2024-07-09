package com.few.api.domain.subscription.usecase.dto

data class UnsubscribeWorkbookUseCaseIn(
    val workbookId: Long,
    val email: String,
    val opinion: String
)
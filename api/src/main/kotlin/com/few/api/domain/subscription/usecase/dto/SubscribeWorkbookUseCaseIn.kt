package com.few.api.domain.subscription.usecase.dto

data class SubscribeWorkbookUseCaseIn(
    val workbookId: Long,
    val email: String
)
package com.few.api.domain.subscription.usecase.dto

data class UnsubscribeAllUseCaseIn(
    val opinion: String,
    val email: String,
)
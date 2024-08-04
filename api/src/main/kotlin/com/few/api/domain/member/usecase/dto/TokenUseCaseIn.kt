package com.few.api.domain.member.usecase.dto

data class TokenUseCaseIn(
    val token: String?,
    val refreshToken: String?,
    val at: Long?,
    val rt: Long?,
)
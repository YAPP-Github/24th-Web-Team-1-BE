package com.few.api.domain.member.usecase.dto

data class TokenUseCaseOut(
    val accessToken: String,
    val refreshToken: String,
    val isLogin: Boolean,
)
package com.few.api.domain.member.controller.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val isLogin: Boolean,
)
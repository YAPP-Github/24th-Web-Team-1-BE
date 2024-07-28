package com.few.api.web.controller.member.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val isLogin: Boolean,
)
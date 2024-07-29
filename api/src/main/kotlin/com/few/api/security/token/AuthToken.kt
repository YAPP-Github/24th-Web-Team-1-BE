package com.few.api.security.token

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
)
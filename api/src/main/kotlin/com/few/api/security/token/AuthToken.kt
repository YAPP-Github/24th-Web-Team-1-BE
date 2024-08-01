package com.few.api.security.token

data class AuthToken(
    private val accessToken: String,
    private val refreshToken: String,
)
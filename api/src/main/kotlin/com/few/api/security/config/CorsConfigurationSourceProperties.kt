package com.few.api.security.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class CorsConfigurationSourceProperties(
    @Value("\${security.cors.path-patterns}")
    val pathPattern: String,
    @Value("\${security.cors.origin-patterns}")
    val originPatterns: String,
    @Value("\${security.cors.allowed-methods}")
    val allowedMethods: String,
    @Value("\${security.cors.allowed-headers}")
    val allowedHeaders: String,
    @Value("\${security.cors.exposed-headers}")
    val exposedHeaders: String,
    @Value("\${security.cors.allow-credentials}")
    val allowCredentials: Boolean,
    @Value("\${security.cors.max-age}")
    val maxAge: Long,
)
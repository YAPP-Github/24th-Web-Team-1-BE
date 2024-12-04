package web.security.config.properties

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class CorsConfigurationSourceProperties(
    @Value("\${web.security.cors.path-patterns}")
    val pathPattern: String,
    @Value("\${web.security.cors.origin-patterns}")
    val originPatterns: String,
    @Value("\${web.security.cors.allowed-methods}")
    val allowedMethods: String,
    @Value("\${web.security.cors.allowed-headers}")
    val allowedHeaders: String,
    @Value("\${web.security.cors.exposed-headers}")
    val exposedHeaders: String,
    @Value("\${web.security.cors.allow-credentials}")
    val allowCredentials: Boolean,
    @Value("\${web.security.cors.max-age}")
    val maxAge: Long,
)
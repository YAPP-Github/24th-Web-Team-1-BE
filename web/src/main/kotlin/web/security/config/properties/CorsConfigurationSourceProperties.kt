package web.security.config.properties

data class CorsConfigurationSourceProperties(
    val pathPattern: String,
    val originPatterns: String,
    val allowedMethods: String,
    val allowedHeaders: String,
    val exposedHeaders: String,
    val allowCredentials: Boolean,
    val maxAge: Long,
)
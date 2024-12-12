package web.security.config

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.OncePerRequestFilter
import web.security.config.properties.CorsConfigurationSourceProperties
import web.security.exception.WebTokenInvalidExceptionHandlerFilter
import web.security.filter.token.TokenAuthenticationFilter

/**
 * Security 설정을 위임하는 인터페이스.
 */
interface AbstractDelegatedSecurityConfigurer {

    /**
     * Security 설정을 반환한다.
     */
    fun securityFilterChain(http: HttpSecurity): DefaultSecurityFilterChain

    /**
     * Security 설정을 무시하는 Customizer를 반환한다.
     */
    fun ignoreCustomizer(): WebSecurityCustomizer

    val authenticationFilter: TokenAuthenticationFilter
        get() {
            val tokenAuthenticationFilter = TokenAuthenticationFilter()
            val tokenAuthProvider = getTokenAuthProvider()
            tokenAuthenticationFilter.setAuthenticationManager(ProviderManager(tokenAuthProvider))
            return tokenAuthenticationFilter
        }

    fun getTokenAuthProvider(): AuthenticationProvider

    val webTokenInvalidExceptionHandlerFilter: OncePerRequestFilter
        get() {
            return WebTokenInvalidExceptionHandlerFilter()
        }

    val corsConfigurationSource: CorsConfigurationSource
        get() {
            val configuration = CorsConfiguration()
            val corsProperties = getCorsProperties()
            configuration.addAllowedOriginPattern(corsProperties.originPatterns)
            configuration.addAllowedHeader(corsProperties.allowedHeaders)
            configuration.addAllowedMethod(corsProperties.allowedMethods)
            configuration.allowCredentials = corsProperties.allowCredentials
            val source = UrlBasedCorsConfigurationSource()
            source.registerCorsConfiguration(corsProperties.pathPattern, configuration)
            return source
        }

    fun getCorsProperties(): CorsConfigurationSourceProperties
}
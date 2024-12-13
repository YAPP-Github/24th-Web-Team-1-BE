package web.security.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import security.TokenGenerator
import security.TokenResolver
import security.authentication.token.TokenAuthProvider
import security.authentication.token.TokenUserDetailsService
import security.token.SecurityTokenGenerator
import security.token.SecurityTokenResolver
import web.config.WebConfig
import web.security.UserArgumentHandlerMethodArgumentResolver
import web.security.config.properties.CorsConfigurationSourceProperties
import web.security.handler.DelegatedAccessDeniedHandler
import web.security.handler.DelegatedAuthenticationEntryPoint

@EnableWebSecurity
@Configuration
@Import(
    ProdDelegatedSecurityConfigurer::class,
    LocalDelegatedSecurityConfigurer::class
)
class WebSecurityConfig {
    companion object {
        const val SECURITY_FILTER_CHAIN = WebConfig.BEAN_NAME_PREFIX + "SecurityFilterChain"
        const val WEB_SECURITY_CUSTOMIZER = WebConfig.BEAN_NAME_PREFIX + "WebSecurityCustomizer"
        const val TOKEN_GENERATOR = WebConfig.BEAN_NAME_PREFIX + "TokenGenerator"
        const val TOKEN_RESOLVER = WebConfig.BEAN_NAME_PREFIX + "TokenResolver"
        const val TOKEN_USER_DETAILS_SERVICE = WebConfig.BEAN_NAME_PREFIX + "TokenUserDetailsService"
        const val TOKEN_AUTH_PROVIDER = WebConfig.BEAN_NAME_PREFIX + "TokenAuthProvider"
        const val USER_ARGUMENT_HANDLER_METHOD_ARGUMENT_RESOLVER = WebConfig.BEAN_NAME_PREFIX + "UserArgumentHandlerMethodArgumentResolver"
        const val DELEGATED_AUTHENTICATION_ENTRY_POINT = WebConfig.BEAN_NAME_PREFIX + "DelegatedAuthenticationEntryPoint"
        const val DELEGATED_ACCESS_DENIED_HANDLER = WebConfig.BEAN_NAME_PREFIX + "DelegatedAccessDeniedHandler"
        const val WEB_SECURITY_CONFIGURER = WebConfig.BEAN_NAME_PREFIX + "WebSecurityConfigurer"
        const val CORS_CONFIGURATION_SOURCE_PROPERTIES = WebConfig.BEAN_NAME_PREFIX + "CorsConfigurationSourceProperties"
        const val LOCAL_DELEGATED_SECURITY_CONFIGURER = "LocalDelegatedSecurityConfigurer"
        const val PROD_DELEGATED_SECURITY_CONFIGURER = "ProdDelegatedSecurityConfigurer"
    }

    @Bean(name = [WEB_SECURITY_CONFIGURER])
    fun webSecurityConfigurer(userArgumentHandlerMethodArgumentResolver: HandlerMethodArgumentResolver): WebMvcConfigurer {
        return WebSecurityConfigurer(userArgumentHandlerMethodArgumentResolver)
    }

    @Profile("local")
    @Bean(name = ["local$SECURITY_FILTER_CHAIN"])
    fun localSecurityFilterChain(@Qualifier(LOCAL_DELEGATED_SECURITY_CONFIGURER) abstractDelegatedSecurityConfigurer: AbstractDelegatedSecurityConfigurer, http: HttpSecurity): SecurityFilterChain {
        return abstractDelegatedSecurityConfigurer.securityFilterChain(http)
    }

    @Profile("local")
    @Bean(name = ["local$WEB_SECURITY_CUSTOMIZER"])
    fun localWebSecurityFilterIgnoreCustomizer(@Qualifier(LOCAL_DELEGATED_SECURITY_CONFIGURER) abstractDelegatedSecurityConfigurer: AbstractDelegatedSecurityConfigurer): WebSecurityCustomizer {
        return abstractDelegatedSecurityConfigurer.ignoreCustomizer()
    }

    @Profile("!local")
    @Bean(name = ["prod$SECURITY_FILTER_CHAIN"])
    fun prodSecurityFilterChain(@Qualifier(PROD_DELEGATED_SECURITY_CONFIGURER) abstractDelegatedSecurityConfigurer: AbstractDelegatedSecurityConfigurer, http: HttpSecurity): SecurityFilterChain {
        return abstractDelegatedSecurityConfigurer.securityFilterChain(http)
    }

    @Profile("!local")
    @Bean(name = ["prod$WEB_SECURITY_CUSTOMIZER"])
    fun prodWebSecurityFilterIgnoreCustomizer(@Qualifier(PROD_DELEGATED_SECURITY_CONFIGURER) abstractDelegatedSecurityConfigurer: AbstractDelegatedSecurityConfigurer): WebSecurityCustomizer {
        return abstractDelegatedSecurityConfigurer.ignoreCustomizer()
    }

    @Profile("local")
    @Bean(name = [LOCAL_DELEGATED_SECURITY_CONFIGURER])
    fun localDelegatedSecurityConfig(
        delegatedAuthenticationEntryPoint: AuthenticationEntryPoint,
        delegatedAccessDeniedHandler: AccessDeniedHandler,
        tokenAuthProvider: AuthenticationProvider,
        corsConfigurationSourceProperties: CorsConfigurationSourceProperties,
    ): LocalDelegatedSecurityConfigurer {
        return LocalDelegatedSecurityConfigurer(
            delegatedAuthenticationEntryPoint,
            delegatedAccessDeniedHandler,
            tokenAuthProvider,
            corsConfigurationSourceProperties
        )
    }

    @Profile("!local")
    @Bean(name = [PROD_DELEGATED_SECURITY_CONFIGURER])
    fun prdDelegatedSecurityConfig(
        delegatedAuthenticationEntryPoint: AuthenticationEntryPoint,
        delegatedAccessDeniedHandler: AccessDeniedHandler,
        tokenAuthProvider: AuthenticationProvider,
        corsConfigurationSourceProperties: CorsConfigurationSourceProperties,
    ): ProdDelegatedSecurityConfigurer {
        return ProdDelegatedSecurityConfigurer(
            delegatedAuthenticationEntryPoint,
            delegatedAccessDeniedHandler,
            tokenAuthProvider,
            corsConfigurationSourceProperties
        )
    }

    @Bean(name = [TOKEN_GENERATOR])
    fun tokenGenerator(
        @Value("\${web.security.jwt.token.secretkey}") secretKey: String,
        @Value("\${web.security.jwt.token.validtime.access}") accessTokenValidTime: Long,
        @Value("\${web.security.jwt.token.validtime.refresh}") refreshTokenValidTime: Long,
    ): TokenGenerator {
        return SecurityTokenGenerator(secretKey, accessTokenValidTime, refreshTokenValidTime)
    }

    @Bean(name = [TOKEN_RESOLVER])
    fun tokenResolver(
        @Value("\${web.security.jwt.token.secretkey}") secretKey: String,
    ): TokenResolver {
        return SecurityTokenResolver(secretKey)
    }

    @Profile("!test")
    @Bean(name = [TOKEN_USER_DETAILS_SERVICE])
    fun tokenUserDetailsService(tokenResolver: TokenResolver): UserDetailsService {
        return TokenUserDetailsService(tokenResolver)
    }

    @Bean(name = [TOKEN_AUTH_PROVIDER])
    fun tokenAuthProvider(tokenUserDetailsService: UserDetailsService): AuthenticationProvider {
        return TokenAuthProvider(tokenUserDetailsService)
    }

    @Bean(name = [USER_ARGUMENT_HANDLER_METHOD_ARGUMENT_RESOLVER])
    fun userArgumentHandlerMethodArgumentResolver(tokenResolver: TokenResolver): HandlerMethodArgumentResolver {
        return UserArgumentHandlerMethodArgumentResolver(tokenResolver)
    }

    @Bean(name = [DELEGATED_AUTHENTICATION_ENTRY_POINT])
    fun delegatedAuthenticationEntryPoint(handlerExceptionResolver: HandlerExceptionResolver): AuthenticationEntryPoint {
        return DelegatedAuthenticationEntryPoint(handlerExceptionResolver)
    }

    @Bean(name = [DELEGATED_ACCESS_DENIED_HANDLER])
    fun delegatedAccessDeniedHandler(handlerExceptionResolver: HandlerExceptionResolver): AccessDeniedHandler {
        return DelegatedAccessDeniedHandler(handlerExceptionResolver)
    }

    @Bean(name = [CORS_CONFIGURATION_SOURCE_PROPERTIES])
    fun corsConfigurationSourceProperties(
        @Value("\${web.security.cors.path-patterns}") pathPattern: String,
        @Value("\${web.security.cors.origin-patterns}") originPatterns: String,
        @Value("\${web.security.cors.allowed-methods}") allowedMethods: String,
        @Value("\${web.security.cors.allowed-headers}") allowedHeaders: String,
        @Value("\${web.security.cors.exposed-headers}") exposedHeaders: String,
        @Value("\${web.security.cors.allow-credentials}") allowCredentials: Boolean,
        @Value("\${web.security.cors.max-age}") maxAge: Long,
    ): CorsConfigurationSourceProperties {
        return CorsConfigurationSourceProperties(
            pathPattern,
            originPatterns,
            allowedMethods,
            allowedHeaders,
            exposedHeaders,
            allowCredentials,
            maxAge
        )
    }
}
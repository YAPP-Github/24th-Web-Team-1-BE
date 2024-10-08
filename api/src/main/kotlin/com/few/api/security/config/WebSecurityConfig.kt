package com.few.api.security.config

import com.few.api.security.authentication.token.TokenAuthProvider
import com.few.api.security.filter.exception.TokenInvalidExceptionHandlerFilter
import com.few.api.security.filter.token.TokenAuthenticationFilter
import com.few.api.security.handler.DelegatedAccessDeniedHandler
import com.few.api.security.handler.DelegatedAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.OncePerRequestFilter

@EnableWebSecurity
@Configuration
class WebSecurityConfig(
    private val authenticationEntryPoint: DelegatedAuthenticationEntryPoint,
    private val accessDeniedHandler: DelegatedAccessDeniedHandler,
    private val tokenAuthProvider: TokenAuthProvider,
    private val corsProperties: CorsConfigurationSourceProperties,
) {

    @Bean
    @Profile("!prod")
    fun localSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf {
            it.disable()
        }
        http.formLogin {
            it.disable()
        }
        http.httpBasic {
            it.disable()
        }
        http.cors {
            it.configurationSource(corsConfigurationSource())
        }
        http.authorizeHttpRequests {
            it.requestMatchers(
                AntPathRequestMatcher("/api/v1/**")
            ).authenticated().anyRequest().denyAll()
        }
        http.addFilterBefore(
            tokenInvalidExceptionHandlerFilter,
            AbstractPreAuthenticatedProcessingFilter::class.java
        )
        http.addFilterAt(
            generateAuthenticationFilter(),
            AbstractPreAuthenticatedProcessingFilter::class.java
        )
        http.exceptionHandling {
            it.authenticationEntryPoint(authenticationEntryPoint)
            it.accessDeniedHandler(accessDeniedHandler)
        }
        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        return http.build()
    }

    @Bean
    @Profile(value = ["prod"])
    fun prdSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf {
            it.disable()
        }
        http.formLogin {
            it.disable()
        }
        http.httpBasic {
            it.disable()
        }
        http.cors {
            it.configurationSource(corsConfigurationSource())
        }
        http.authorizeHttpRequests {
            it.requestMatchers(
                AntPathRequestMatcher("/api/v1/**")
            ).authenticated().anyRequest().denyAll()
        }
        http.addFilterBefore(
            tokenInvalidExceptionHandlerFilter,
            AbstractPreAuthenticatedProcessingFilter::class.java
        )
        http.addFilterAt(
            generateAuthenticationFilter(),
            AbstractPreAuthenticatedProcessingFilter::class.java
        )
        http.exceptionHandling {
            it.authenticationEntryPoint(authenticationEntryPoint)
            it.accessDeniedHandler(accessDeniedHandler)
        }
        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }
        return http.build()
    }

    private fun generateAuthenticationFilter(): TokenAuthenticationFilter {
        val tokenAuthenticationFilter = TokenAuthenticationFilter()
        tokenAuthenticationFilter.setAuthenticationManager(ProviderManager(tokenAuthProvider))
        return tokenAuthenticationFilter
    }

    val tokenInvalidExceptionHandlerFilter: OncePerRequestFilter
        get() = TokenInvalidExceptionHandlerFilter()

    @Bean
    @Profile("!prod")
    fun localWebSecurityFilterIgnoreCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers(
                    AntPathRequestMatcher("/actuator/health", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/error", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/docs/swagger-ui/*", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/swagger-ui/*", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/swagger-resources/**", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/v3/api-docs/**", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/openapi3.yaml", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/reports/**", HttpMethod.GET.name()),

                    /** 인증/비인증 모두 허용 */
                    AntPathRequestMatcher(
                        "/api/v1/subscriptions/workbooks/main",
                        HttpMethod.GET.name()
                    ),
                    AntPathRequestMatcher("/api/v1/workbooks", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/articles/*", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/workbooks/*/articles/*", HttpMethod.GET.name()),

                    /** 어드민 */
                    AntPathRequestMatcher("/api/v1/admin/**", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/api/v1/articles/views", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/api/v1/logs", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/api/v1/logs/email/articles", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/batch/**"),

                    /** 인증 불필요 */
                    AntPathRequestMatcher("/api/v1/members", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/api/v1/members/token", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/api/v1/articles", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/articles/categories", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/workbooks/categories", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/workbooks/*", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/workbooks/categories", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/workbooks/*/articles/*", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/problems/**", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/problems/*", HttpMethod.POST.name())
                )
        }
    }

    @Bean
    @Profile("prd")
    fun prdWebSecurityFilterIgnoreCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers(
                    AntPathRequestMatcher("/actuator/health", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/error", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/docs/swagger-ui/*", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/swagger-ui/*", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/swagger-resources/**", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/v3/api-docs/**", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/openapi3.yaml", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/reports/**", HttpMethod.GET.name()),

                    /** 인증/비인증 모두 허용 */
                    AntPathRequestMatcher(
                        "/api/v1/subscriptions/workbooks/main",
                        HttpMethod.GET.name()
                    ),
                    AntPathRequestMatcher("/api/v1/workbooks", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/articles/*", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/workbooks/*/articles/*", HttpMethod.GET.name()),

                    /** 어드민 */
                    AntPathRequestMatcher("/api/v1/admin/**", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/api/v1/articles/views", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/api/v1/logs/email/articles", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/api/v1/logs", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/batch/**"),

                    /** 인증 불필요 */
                    AntPathRequestMatcher("/api/v1/members", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/api/v1/members/token", HttpMethod.POST.name()),
                    AntPathRequestMatcher("/api/v1/articles", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/articles/categories", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/workbooks/categories", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/workbooks/*", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/workbooks/categories", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/workbooks/*/articles/*", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/problems/**", HttpMethod.GET.name()),
                    AntPathRequestMatcher("/api/v1/problems/*", HttpMethod.POST.name())
                )
        }
    }

    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.addAllowedOriginPattern(corsProperties.originPatterns)
        configuration.addAllowedHeader(corsProperties.allowedHeaders)
        configuration.addAllowedMethod(corsProperties.allowedMethods)
        configuration.allowCredentials = corsProperties.allowCredentials
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration(corsProperties.pathPattern, configuration)
        return source
    }
}
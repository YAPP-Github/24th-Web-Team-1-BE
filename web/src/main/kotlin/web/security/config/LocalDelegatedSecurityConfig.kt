package web.security.config

import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Component
import security.authentication.token.TokenAuthProvider
import web.security.config.properties.CorsConfigurationSourceProperties
import web.security.handler.DelegatedAccessDeniedHandler
import web.security.handler.DelegatedAuthenticationEntryPoint

@Component
@Profile("!prod")
class LocalDelegatedSecurityConfig(
    private val authenticationEntryPoint: DelegatedAuthenticationEntryPoint,
    private val accessDeniedHandler: DelegatedAccessDeniedHandler,
    private val tokenAuthProvider: TokenAuthProvider,
    private val corsProperties: CorsConfigurationSourceProperties,
) : AbstractDelegatedSecurityConfig {

    override fun securityFilterChain(http: HttpSecurity): DefaultSecurityFilterChain {
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
            it.configurationSource(corsConfigurationSource)
        }
        http.authorizeHttpRequests {
            it.requestMatchers(
                AntPathRequestMatcher("/api/v1/**")
            ).authenticated().anyRequest().denyAll()
        }
        http.addFilterBefore(
            webTokenInvalidExceptionHandlerFilter,
            AbstractPreAuthenticatedProcessingFilter::class.java
        )
        http.addFilterAt(
            authenticationFilter,
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

    override fun ignoreCustomizer(): WebSecurityCustomizer {
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

    override fun getTokenAuthProvider(): TokenAuthProvider {
        return tokenAuthProvider
    }

    override fun getCorsProperties(): CorsConfigurationSourceProperties {
        return corsProperties
    }
}
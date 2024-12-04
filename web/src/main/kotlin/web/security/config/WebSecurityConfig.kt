package web.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.web.SecurityFilterChain
import web.config.WebConfig

@EnableWebSecurity
@Configuration
@Import(
    ProdDelegatedSecurityConfig::class,
    LocalDelegatedSecurityConfig::class
)
class WebSecurityConfig(
    private val securityFilterChainDelegator: AbstractDelegatedSecurityConfig,
) {
    companion object {
        const val SECURITY_FILTER_CHAIN = WebConfig.BEAN_NAME_PREFIX + "SecurityFilterChain"
        const val WEB_SECURITY_CUSTOMIZER = WebConfig.BEAN_NAME_PREFIX + "WebSecurityCustomizer"
    }

    @Bean(name = [SECURITY_FILTER_CHAIN])
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return securityFilterChainDelegator.securityFilterChain(http)
    }

    @Bean(name = [WEB_SECURITY_CUSTOMIZER])
    fun webSecurityFilterIgnoreCustomizer(): WebSecurityCustomizer {
        return securityFilterChainDelegator.ignoreCustomizer()
    }
}
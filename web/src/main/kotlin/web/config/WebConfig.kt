package web.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import security.config.SecurityConfig

@Configuration
@ComponentScan(basePackages = [WebConfig.BASE_PACKAGE])
@Import(
    SecurityConfig::class
)
class WebConfig {
    companion object {
        const val BASE_PACKAGE = "web"
        const val BEAN_NAME_PREFIX = "web"
    }
}
package web.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.Filter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import security.config.SecurityConfig
import web.filter.MDCLogFilter
import web.handler.LoggingHandler

@Configuration
@ComponentScan(basePackages = [WebConfig.BASE_PACKAGE])
@Import(
    SecurityConfig::class,
)
class WebConfig {
    companion object {
        const val BASE_PACKAGE = "web"
        const val BEAN_NAME_PREFIX = "web"
        const val WEB_CONFIGURER = BEAN_NAME_PREFIX + "Configurer"
        const val MDC_LOG_FILTER = BEAN_NAME_PREFIX + "MdcLogFilter"
        const val LOGGING_HANDLER = BEAN_NAME_PREFIX + "LoggingHandler"
    }

    @Bean(name = [WEB_CONFIGURER])
    fun webConfigurer(): WebMvcConfigurer = WebConfigurer()

    @Bean(name = [MDC_LOG_FILTER])
    fun mdcLogFilter(objectMapper: ObjectMapper): Filter = MDCLogFilter(objectMapper)

    @Bean(name = [LOGGING_HANDLER])
    fun loggingHandler(): LoggingHandler = LoggingHandler()
}
package com.few.api.web.config

import com.few.api.web.config.converter.*
import com.few.api.web.support.method.UserArgumentHandlerMethodArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val userArgumentHandlerMethodArgumentResolver: UserArgumentHandlerMethodArgumentResolver,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns(CorsConfiguration.ALL)
            .allowedMethods(CorsConfiguration.ALL)
            .allowedHeaders(CorsConfiguration.ALL)
            .allowCredentials(true)
            .maxAge(3600)
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
    }

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(WorkBookCategoryConverter())
        registry.addConverter(ViewConverter())
        registry.addConverter(DayCodeConverter())
        registry.addConverter(EmailLogEventTypeConverter())
        registry.addConverter(SendTypeConverter())
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(userArgumentHandlerMethodArgumentResolver)
    }
}
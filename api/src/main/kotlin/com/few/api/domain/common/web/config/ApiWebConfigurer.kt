package com.few.api.domain.common.web.config

import com.few.api.domain.common.web.config.converter.*
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class ApiWebConfigurer : WebMvcConfigurer {

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(WorkBookCategoryConverter())
        registry.addConverter(ViewConverter())
        registry.addConverter(DayCodeConverter())
        registry.addConverter(EmailLogEventTypeConverter())
        registry.addConverter(SendTypeConverter())
    }
}
package com.few.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.nio.charset.StandardCharsets

@Configuration
class ApiMessageSourceConfig {
    @Bean
    fun messageSource(): ReloadableResourceBundleMessageSource {
        val messageSource = ReloadableResourceBundleMessageSource()
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name())
        for (path in MESSAGE_SOURCE_CLASSPATH_LIST) {
            messageSource.addBasenames(path)
        }
        return messageSource
    }

    companion object {
        private val MESSAGE_SOURCE_CLASSPATH_LIST =
            listOf(
                "classpath:messages/api/article",
                "classpath:messages/api/document",
                "classpath:messages/api/external",
                "classpath:messages/api/image",
                "classpath:messages/api/member",
                "classpath:messages/api/problem",
                "classpath:messages/api/submit",
                "classpath:messages/api/subscribe",
                "classpath:messages/api/workbook",
            )
    }
}
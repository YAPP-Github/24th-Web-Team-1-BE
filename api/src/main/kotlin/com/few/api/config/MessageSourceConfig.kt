package com.few.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.nio.charset.StandardCharsets

@Configuration
class MessageSourceConfig {
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
        private val MESSAGE_SOURCE_CLASSPATH_LIST = listOf(
            "classpath:messages/article",
            "classpath:messages/document",
            "classpath:messages/external",
            "classpath:messages/image",
            "classpath:messages/member",
            "classpath:messages/problem",
            "classpath:messages/submit",
            "classpath:messages/subscribe",
            "classpath:messages/workbook"
        )
    }
}
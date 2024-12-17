package com.few.domain.generator.config

import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenAiFeignConfiguration(
    @Value("\${openai.api.key}") private val apiKey: String,
) {
    @Bean
    fun requestInterceptor(): RequestInterceptor =
        RequestInterceptor { template ->
            template.header("Authorization", "Bearer $apiKey")
            template.header("Content-Type", "application/json")
        }
}
package com.few.api.client.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class ClientConfig {

    @Bean
    fun restTemplate(
        restTemplateBuilder: RestTemplateBuilder,
        @Value("\${client.timeout.connect}") connectTimeout: Int,
        @Value("\${client.timeout.read}") readTimeout: Int
    ): RestTemplate {
        return restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(connectTimeout.toLong()))
            .setReadTimeout(Duration.ofSeconds(readTimeout.toLong()))
            .build()
    }
}
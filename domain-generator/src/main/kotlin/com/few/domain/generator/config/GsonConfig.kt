package com.few.domain.generator.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GsonConfig {
    @Bean
    fun fewGson(): Gson =
        GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()
}
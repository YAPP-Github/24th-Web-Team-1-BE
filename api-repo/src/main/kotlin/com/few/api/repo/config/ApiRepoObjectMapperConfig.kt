package com.few.api.repo.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApiRepoObjectMapperConfig {

    companion object {
        const val OBJECT_MAPPER = ApiRepoConfig.BEAN_NAME_PREFIX + "ObjectMapper"
    }

    // todo study about ObjectMapper configuration
    @Bean(name = [OBJECT_MAPPER])
    fun customizeJson(): ObjectMapper {
        return ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
            .registerKotlinModule()
    }
}
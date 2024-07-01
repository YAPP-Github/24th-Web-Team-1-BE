package com.few.api.config

import com.few.api.repo.config.ApiRepoConfig
import com.few.batch.config.BatchConfig
import com.few.storage.image.config.ImageStorageConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(basePackages = [ApiConfig.BASE_PACKAGE])
@Import(ApiRepoConfig::class, BatchConfig::class, ImageStorageConfig::class)
@EnableWebMvc
class ApiConfig {
    companion object {
        const val BASE_PACKAGE = "com.few.api"
        const val SERVICE_NAME = "few"
        const val MODULE_NAME = "few-api"
        const val BEAN_NAME_PREFIX = "fewApi"
        const val PROPERTY_PREFIX = SERVICE_NAME + "." + MODULE_NAME
    }
}
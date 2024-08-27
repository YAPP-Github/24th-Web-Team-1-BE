package com.few.api.config

import com.few.api.repo.config.ApiRepoConfig
import com.few.batch.config.BatchConfig
import com.few.email.config.MailConfig
import com.few.storage.document.config.DocumentStorageConfig
import com.few.storage.image.config.ImageStorageConfig
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableAsync

@Configuration
@ComponentScan(basePackages = [ApiConfig.BASE_PACKAGE])
@Import(
    ApiRepoConfig::class,
    BatchConfig::class,
    MailConfig::class,
    ImageStorageConfig::class,
    DocumentStorageConfig::class
)
@EnableAsync
@ConfigurationPropertiesScan(basePackages = [ApiConfig.BASE_PACKAGE])
class ApiConfig {
    companion object {
        const val BASE_PACKAGE = "com.few.api"
        const val SERVICE_NAME = "few"
        const val MODULE_NAME = "few-api"
        const val BEAN_NAME_PREFIX = "fewApi"
        const val PROPERTY_PREFIX = SERVICE_NAME + "." + MODULE_NAME
    }
}
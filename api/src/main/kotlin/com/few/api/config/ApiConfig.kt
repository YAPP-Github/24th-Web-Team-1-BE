package com.few.api.config

import email.config.MailConfig
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import repo.config.RepoConfig
import security.config.SecurityConfig
import storage.document.config.DocumentStorageConfig
import storage.image.config.ImageStorageConfig
import web.config.WebConfig

@Configuration
@ComponentScan(basePackages = [ApiConfig.BASE_PACKAGE])
@Import(
    RepoConfig::class,
    MailConfig::class,
    ImageStorageConfig::class,
    DocumentStorageConfig::class,
    WebConfig::class,
    SecurityConfig::class,
)
@ConfigurationPropertiesScan(basePackages = [ApiConfig.BASE_PACKAGE])
class ApiConfig {
    companion object {
        const val BASE_PACKAGE = "com.few.api"
        const val BEAN_NAME_PREFIX = "fewApi"
    }
}
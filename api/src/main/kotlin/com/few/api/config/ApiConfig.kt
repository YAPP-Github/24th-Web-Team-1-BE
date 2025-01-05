package com.few.api.config

import com.few.crm.config.CrmConfig
import email.config.MailConfig
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import security.config.SecurityConfig
import storage.document.config.DocumentStorageConfig
import storage.image.config.ImageStorageConfig
import web.config.WebConfig

@Configuration
@ComponentScan(basePackages = [ApiConfig.BASE_PACKAGE])
@Import(
    MailConfig::class,
    ImageStorageConfig::class,
    DocumentStorageConfig::class,
    WebConfig::class,
    SecurityConfig::class,
    CrmConfig::class,
)
@ConfigurationPropertiesScan(basePackages = [ApiConfig.BASE_PACKAGE])
class ApiConfig {
    companion object {
        const val BASE_PACKAGE = "com.few.api"
        const val BEAN_NAME_PREFIX = "fewApi"
    }
}
package com.few.storage.document.config

import com.few.storage.config.ClientConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(basePackages = [DocumentStorageConfig.BASE_PACKAGE])
@Import(ClientConfig::class)
class DocumentStorageConfig {
    companion object {
        const val BASE_PACKAGE = "com.few.storage.document"
        const val SERVICE_NAME = "documentStorage"
        const val MODULE_NAME = "storage-document"
        const val BEAN_NAME_PREFIX = "documentStore"
        const val PROPERTY_PREFIX = SERVICE_NAME + "." + MODULE_NAME
    }
}
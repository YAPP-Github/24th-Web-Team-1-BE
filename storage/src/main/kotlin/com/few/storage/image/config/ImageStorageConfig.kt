package com.few.storage.image.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [ImageStorageConfig.BASE_PACKAGE])
class ImageStorageConfig {
    companion object {
        const val BASE_PACKAGE = "com.few.storage.image"
        const val SERVICE_NAME = "imageStorage"
        const val MODULE_NAME = "storage-image"
        const val BEAN_NAME_PREFIX = "imageStore"
        const val PROPERTY_PREFIX = SERVICE_NAME + "." + MODULE_NAME
    }
}
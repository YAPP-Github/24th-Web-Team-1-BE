package com.few.data.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [DataConfig.BASE_PACKAGE])
class DataConfig {
    companion object {
        const val BASE_PACKAGE = "com.few.data"
        const val SERVICE_NAME = "few"
        const val MODULE_NAME = "few-data"
        const val BEAN_NAME_PREFIX = "fewData"
        const val PROPERTY_PREFIX = SERVICE_NAME + "." + MODULE_NAME
    }
}

package com.few.api.repo.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [ApiRepoConfig.BASE_PACKAGE])
class ApiRepoConfig {
    companion object {
        const val BASE_PACKAGE = "com.few.api.repo"
        const val SERVICE_NAME = "few"
        const val MODULE_NAME = "few-api-repo"
        const val BEAN_NAME_PREFIX = "fewApiRepo"
        const val PROPERTY_PREFIX = SERVICE_NAME + "." + MODULE_NAME
    }
}

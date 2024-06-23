package com.few.batch.config

import com.few.email.config.MailConfig
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(basePackages = [BatchConfig.BASE_PACKAGE])
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class])
@Import(MailConfig::class)
class BatchConfig {
    companion object {
        const val BASE_PACKAGE = "com.few.batch"
        const val SERVICE_NAME = "few"
        const val MODULE_NAME = "few-batch"
        const val BEAN_NAME_PREFIX = "fewBatch"
        const val PROPERTY_PREFIX = SERVICE_NAME + "." + MODULE_NAME
    }
}
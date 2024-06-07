package com.few.data.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

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

    @Primary
    @Bean(name = [DataConfig.BEAN_NAME_PREFIX + "DataSource"])
    @ConfigurationProperties(prefix = "spring.datasource")
    fun dataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean(name = [DataConfig.BEAN_NAME_PREFIX + "TransactionManager"])
    fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }
}

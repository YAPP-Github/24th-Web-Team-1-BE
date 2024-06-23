package com.few.batch

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@TestConfiguration
class BatchTestDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    fun batchTestDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean
    fun batchTestTransactionManager(): PlatformTransactionManager {
        return DataSourceTransactionManager(batchTestDataSource())
    }
}
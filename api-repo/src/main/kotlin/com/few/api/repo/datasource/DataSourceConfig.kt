package com.few.api.repo.datasource

import com.few.api.repo.config.ApiRepoConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class DataSourceConfig {

    companion object {
        const val API_DATASOURCE = ApiRepoConfig.BEAN_NAME_PREFIX + "DataSource"
        const val API_TX = ApiRepoConfig.BEAN_NAME_PREFIX + "TransactionManager"
    }

    @Bean(name = [API_DATASOURCE])
    @ConfigurationProperties(prefix = "spring.datasource")
    fun apiDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean(name = [API_TX])
    fun apiTransactionManager(): PlatformTransactionManager {
        return DataSourceTransactionManager(apiDataSource())
    }
}

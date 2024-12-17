package repo.config

import com.zaxxer.hikari.HikariDataSource
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
        const val API_DATASOURCE = RepoConfig.BEAN_NAME_PREFIX + "DataSource"
        const val API_TX = RepoConfig.BEAN_NAME_PREFIX + "TransactionManager"
    }

    @Bean(name = [API_DATASOURCE])
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    fun apiDataSource(): DataSource = DataSourceBuilder.create().type(HikariDataSource::class.java).build()

    @Bean(name = [API_TX])
    fun apiTransactionManager(): PlatformTransactionManager = DataSourceTransactionManager(apiDataSource())
}
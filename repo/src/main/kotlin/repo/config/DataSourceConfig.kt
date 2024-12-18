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
        const val DATASOURCE = RepoConfig.BEAN_NAME_PREFIX + "DataSource"
        const val DATASOURCE_TX = RepoConfig.BEAN_NAME_PREFIX + "DataSourceTransactionManager"
    }

    @Bean(name = [DATASOURCE])
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    fun dataSource(): DataSource = DataSourceBuilder.create().type(HikariDataSource::class.java).build()

    @Bean(name = [DATASOURCE_TX])
    fun transactionManager(): PlatformTransactionManager = DataSourceTransactionManager(dataSource())
}
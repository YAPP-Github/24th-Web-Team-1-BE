package repo.config

import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import org.springframework.transaction.PlatformTransactionManager
import repo.config.DataSourceConfig.Companion.DATASOURCE
import repo.config.DataSourceConfig.Companion.DATASOURCE_TX
import repo.flyway.support.ExceptionTranslator
import repo.flyway.support.NativeSQLLogger
import repo.flyway.support.PerformanceListener
import javax.sql.DataSource

@Configuration
@Import(DataSourceConfig::class)
class JooqConfig(
    @Qualifier(DATASOURCE) private val dataSource: DataSource,
    @Qualifier(DATASOURCE_TX) private val txManager: PlatformTransactionManager,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    companion object {
        const val DSL = RepoConfig.BEAN_NAME_PREFIX + "Dsl"
        const val JOOQ_CONFIGURATION = RepoConfig.BEAN_NAME_PREFIX + "JooqConfiguration"
        const val JOOQ_CONNECTION_PROVIDER = RepoConfig.BEAN_NAME_PREFIX + "JooqConnectionProvider"
    }

    @Bean(name = [DSL])
    fun dsl(): DefaultDSLContext = DefaultDSLContext(configuration())

    @Bean(name = [JOOQ_CONFIGURATION])
    fun configuration(): DefaultConfiguration {
        val jooqConfiguration = DefaultConfiguration()
        jooqConfiguration.set(connectionProvider())
        val translator =
            SQLErrorCodeSQLExceptionTranslator(SQLDialect.MYSQL.name)
        jooqConfiguration.set(ExceptionTranslator(translator), NativeSQLLogger(), PerformanceListener(applicationEventPublisher))
        jooqConfiguration.set(SQLDialect.MYSQL)
        return jooqConfiguration
    }

    @Bean(name = [JOOQ_CONNECTION_PROVIDER])
    fun connectionProvider(): DataSourceConnectionProvider = DataSourceConnectionProvider(dataSource)

    @Bean
    fun transactionProvider(): SpringTransactionProvider = SpringTransactionProvider(txManager)
}
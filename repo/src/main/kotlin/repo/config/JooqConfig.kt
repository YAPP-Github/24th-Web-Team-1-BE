package repo.config

import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
import repo.flyway.support.ExceptionTranslator
import repo.flyway.support.NativeSQLLogger
import repo.flyway.support.PerformanceListener
import javax.sql.DataSource

@Configuration
class JooqConfig(
    private val dataSource: DataSource,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    companion object {
        const val DSL = RepoConfig.BEAN_NAME_PREFIX + "Dsl"
        const val JOOQ_CONFIGURATION = RepoConfig.BEAN_NAME_PREFIX + "JooqConfiguration"
        const val JOOQ_CONNECTION_PROVIDER = RepoConfig.BEAN_NAME_PREFIX + "JooqConnectionProvider"
    }

    @Bean(name = [DSL])
    fun dsl(): DefaultDSLContext {
        return DefaultDSLContext(configuration())
    }

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
    fun connectionProvider(): DataSourceConnectionProvider {
        return DataSourceConnectionProvider(dataSource)
    }
}
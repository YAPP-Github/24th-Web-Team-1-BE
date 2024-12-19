package repo.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.TransactionManagementConfigurer
import repo.config.DataSourceConfig.Companion.DATASOURCE
import javax.sql.DataSource

@Import(JpaConfig::class, DataSourceConfig::class)
@Configuration
@EnableTransactionManagement
class TxConfig(
    private val emf: EntityManagerFactory,
    @Qualifier(DATASOURCE) private val dataSource: DataSource,
) : TransactionManagementConfigurer {
    companion object {
        const val JPA_TX = RepoConfig.BEAN_NAME_PREFIX + "JpaTransactionManager"
        const val DATASOURCE_TX = RepoConfig.BEAN_NAME_PREFIX + "DataSourceTransactionManager"
    }

    @Bean(name = [JPA_TX])
    fun jpaTransactionManager(): PlatformTransactionManager = JpaTransactionManager(emf)

    @Bean(name = [DATASOURCE_TX])
    fun dataSourceTransactionManager(): PlatformTransactionManager = DataSourceTransactionManager(dataSource)

    override fun annotationDrivenTransactionManager(): TransactionManager = jpaTransactionManager()
}
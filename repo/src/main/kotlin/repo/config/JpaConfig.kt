package repo.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableAutoConfiguration(
    exclude = [
        DataSourceAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
        HibernateJpaAutoConfiguration::class,
    ],
)
@EnableJpaAuditing
@EnableTransactionManagement
class JpaConfig {
    companion object {
        const val ENTITY_UNIT = "few"
        const val ENTITY_PACKAGE = "com.few"
        const val JPA_TX = RepoConfig.BEAN_NAME_PREFIX + "JpaTransactionManager"
        const val JPA_EMF = RepoConfig.BEAN_NAME_PREFIX + "JpaEntityManagerFactory"
    }

    @Bean(name = [JPA_EMF])
    fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val jpaPropertyMap = jpaProperties().properties
        val hibernatePropertyMap =
            hibernateProperties().determineHibernateProperties(jpaPropertyMap, HibernateSettings())

        return EntityManagerFactoryBuilder(HibernateJpaVendorAdapter(), jpaPropertyMap, null)
            .dataSource(dataSource)
            .properties(hibernatePropertyMap)
            .persistenceUnit(ENTITY_UNIT)
            .packages(ENTITY_PACKAGE)
            .build()
    }

    @Bean(name = [JPA_TX])
    fun jpaTransactionManager(emf: EntityManagerFactory): PlatformTransactionManager = JpaTransactionManager(emf)

    @Bean
    @ConfigurationProperties(prefix = "spring.jpa")
    fun jpaProperties(): JpaProperties = JpaProperties()

    @Bean
    @ConfigurationProperties(prefix = "spring.jpa.hibernate")
    fun hibernateProperties(): HibernateProperties = HibernateProperties()
}
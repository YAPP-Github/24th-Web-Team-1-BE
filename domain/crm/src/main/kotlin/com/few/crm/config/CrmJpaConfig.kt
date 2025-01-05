package com.few.crm.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = [CrmConfig.BASE_PACKAGE],
    entityManagerFactoryRef = CrmJpaConfig.ENTITY_MANAGER_FACTORY,
    transactionManagerRef = CrmJpaConfig.TRANSACTION_MANAGER,
)
@Import(
    value = [
        CrmDataSourceConfig::class,
    ],
)
class CrmJpaConfig {
    companion object {
        const val JPA_PROPERTIES = CrmConfig.BEAN_NAME_PREFIX + "JpaProperties"
        const val HIBERNATE_PROPERTIES = CrmConfig.BEAN_NAME_PREFIX + "HibernateProperties"
        const val JPA_VENDOR_ADAPTER = CrmConfig.BEAN_NAME_PREFIX + "JpaVendorAdapter"
        const val ENTITY_MANAGER_FACTORY_BUILDER = CrmConfig.BEAN_NAME_PREFIX + "EntityManagerFactoryBuilder"
        const val ENTITY_MANAGER_FACTORY = CrmConfig.BEAN_NAME_PREFIX + "EntityManagerFactory"
        const val TRANSACTION_MANAGER = CrmConfig.BEAN_NAME_PREFIX + "TransactionManager"
    }

    @Bean(name = [ENTITY_MANAGER_FACTORY])
    fun entityManagerFactory(
        @Qualifier(CrmDataSourceConfig.DATASOURCE)
        dataSource: DataSource,
        @Qualifier(ENTITY_MANAGER_FACTORY_BUILDER)
        builder: EntityManagerFactoryBuilder,
    ): LocalContainerEntityManagerFactoryBean {
        val jpaPropertyMap = jpaProperties().properties
        val hibernatePropertyMap =
            hibernateProperties().determineHibernateProperties(jpaPropertyMap, HibernateSettings())

        return builder
            .dataSource(dataSource)
            .properties(hibernatePropertyMap)
            .packages(CrmConfig.BASE_PACKAGE)
            .build()
    }

    @Bean(name = [TRANSACTION_MANAGER])
    fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager = JpaTransactionManager(emf)

    @Bean(name = [JPA_PROPERTIES])
    @ConfigurationProperties("spring.crm.jpa")
    fun jpaProperties(): JpaProperties = JpaProperties()

    @Bean(name = [HIBERNATE_PROPERTIES])
    @ConfigurationProperties("spring.crm.jpa.hibernate")
    fun hibernateProperties(): HibernateProperties = HibernateProperties()

    @Bean(name = [JPA_VENDOR_ADAPTER])
    fun jpaVendorAdapter(): JpaVendorAdapter = HibernateJpaVendorAdapter()

    @Bean(name = [ENTITY_MANAGER_FACTORY_BUILDER])
    fun entityManagerFactoryBuilder(
        @Qualifier(JPA_VENDOR_ADAPTER)
        jpaVendorAdapter: JpaVendorAdapter,
        @Qualifier(JPA_PROPERTIES)
        jpaProperties: JpaProperties,
        persistenceUnitManager: ObjectProvider<PersistenceUnitManager>,
    ): EntityManagerFactoryBuilder {
        val jpaPropertyMap = jpaProperties.properties
        return EntityManagerFactoryBuilder(
            jpaVendorAdapter,
            jpaPropertyMap,
            persistenceUnitManager.getIfAvailable(),
        )
    }
}
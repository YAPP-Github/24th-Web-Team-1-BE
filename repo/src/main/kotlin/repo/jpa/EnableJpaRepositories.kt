package repo.jpa

import org.springframework.context.annotation.ComponentScan
import org.springframework.core.annotation.AliasFor
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean
import org.springframework.data.repository.config.BootstrapMode
import org.springframework.data.repository.config.DefaultRepositoryBaseClass
import org.springframework.data.repository.query.QueryLookupStrategy
import repo.config.JpaConfig
import repo.config.TxConfig
import kotlin.reflect.KClass

/**
 * org.springframework.data.jpa.repository.config.EnableJpaRepositories를 대체하는 애노테이션
 * transactionManagerRef와 entityManagerFactoryRef는 TxConfig와 JpaConfig에서 설정한 값을 기본으로 사용한다.
 * @see EnableJpaRepositories
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@EnableJpaRepositories(
    transactionManagerRef = TxConfig.JPA_TX,
    entityManagerFactoryRef = JpaConfig.JPA_EMF,
)
annotation class EnableJpaRepositories(
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "value")
    val value: Array<String> = [],
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "basePackages")
    val basePackages: Array<String> = [],
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "includeFilters")
    val includeFilters: Array<ComponentScan.Filter> = [],
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "excludeFilters")
    val excludeFilters: Array<ComponentScan.Filter> = [],
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "repositoryImplementationPostfix")
    val repositoryImplementationPostfix: String = "Impl",
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "namedQueriesLocation")
    val namedQueriesLocation: String = "",
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "queryLookupStrategy")
    val queryLookupStrategy: QueryLookupStrategy.Key = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND,
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "repositoryFactoryBeanClass")
    val repositoryFactoryBeanClass: KClass<*> = JpaRepositoryFactoryBean::class,
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "repositoryBaseClass")
    val repositoryBaseClass: KClass<*> = DefaultRepositoryBaseClass::class,
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "considerNestedRepositories")
    val considerNestedRepositories: Boolean = false,
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "enableDefaultTransactions")
    val enableDefaultTransactions: Boolean = true,
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "bootstrapMode")
    val bootstrapMode: BootstrapMode = BootstrapMode.DEFAULT,
    @get:AliasFor(annotation = EnableJpaRepositories::class, attribute = "escapeCharacter")
    val escapeCharacter: Char = '\\',
)
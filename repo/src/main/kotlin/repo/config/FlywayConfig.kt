package repo.config

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import java.util.function.Consumer
import javax.sql.DataSource

@Configuration
@Import(DataSourceConfig::class)
class FlywayConfig {
    companion object {
        const val FLYWAY = RepoConfig.BEAN_NAME_PREFIX + "Flyway"
        const val FLYWAY_VALIDATE_INITIALIZER = RepoConfig.BEAN_NAME_PREFIX + "FlywayValidateInitializer"
        const val FLYWAY_MIGRATION_INITIALIZER = RepoConfig.BEAN_NAME_PREFIX + "FlywayMigrationInitializer"
        const val FLYWAY_PROPERTIES = RepoConfig.BEAN_NAME_PREFIX + "FlywayProperties"
        const val FLYWAY_CONFIGURATION = RepoConfig.BEAN_NAME_PREFIX + "FlywayConfiguration"
    }

    @Bean(name = [FLYWAY])
    fun flyway(configuration: org.flywaydb.core.api.configuration.Configuration?): Flyway = Flyway(configuration)

    @Profile("!new")
    @Bean(name = [FLYWAY_VALIDATE_INITIALIZER])
    fun flywayValidateInitializer(flyway: Flyway?): FlywayMigrationInitializer =
        FlywayMigrationInitializer(flyway) { obj: Flyway -> obj.validate() }

    @Bean(name = [FLYWAY_MIGRATION_INITIALIZER])
    fun flywayMigrationInitializer(flyway: Flyway?): FlywayMigrationInitializer =
        FlywayMigrationInitializer(flyway) { obj: Flyway -> obj.migrate() }

    @Bean(name = [FLYWAY_PROPERTIES])
    @ConfigurationProperties(prefix = "spring.flyway")
    fun flywayProperties(): FlywayProperties = FlywayProperties()

    @Bean(name = [FLYWAY_CONFIGURATION])
    fun configuration(
        @Qualifier(DataSourceConfig.API_DATASOURCE) dataSource: DataSource?,
    ): org.flywaydb.core.api.configuration.Configuration {
        val configuration = FluentConfiguration()
        configuration.dataSource(dataSource)
        flywayProperties().locations.forEach(
            Consumer { locations: String? ->
                configuration.locations(
                    locations,
                )
            },
        )
        return configuration
    }
}
package com.few.api.repo.flyway

import com.few.api.repo.config.ApiRepoConfig
import com.few.api.repo.datasource.DataSourceConfig
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
    @Bean(name = [ApiRepoConfig.BEAN_NAME_PREFIX + "Flyway"])
    fun flyway(
        configuration: org.flywaydb.core.api.configuration.Configuration?,
    ): Flyway {
        return Flyway(configuration)
    }

    @Profile("!new")
    @Bean(name = [ApiRepoConfig.BEAN_NAME_PREFIX + "FlywayValidateInitializer"])
    fun flywayValidateInitializer(
        flyway: Flyway?,
    ): FlywayMigrationInitializer {
        return FlywayMigrationInitializer(flyway) { obj: Flyway -> obj.validate() }
    }

    @Bean(name = [ApiRepoConfig.BEAN_NAME_PREFIX + "FlywayMigrationInitializer"])
    fun flywayMigrationInitializer(
        flyway: Flyway?,
    ): FlywayMigrationInitializer {
        return FlywayMigrationInitializer(flyway) { obj: Flyway -> obj.migrate() }
    }

    @Bean(name = [ApiRepoConfig.BEAN_NAME_PREFIX + "FlywayProperties"])
    @ConfigurationProperties(prefix = "spring.flyway")
    fun flywayProperties(): FlywayProperties {
        return FlywayProperties()
    }

    @Bean(name = [ApiRepoConfig.BEAN_NAME_PREFIX + "FlywayConfiguration"])
    fun configuration(
        @Qualifier(DataSourceConfig.API_DATASOURCE) dataSource: DataSource?,
    ): org.flywaydb.core.api.configuration.Configuration {
        val configuration = FluentConfiguration()
        configuration.dataSource(dataSource)
        flywayProperties().locations.forEach(
            Consumer { locations: String? ->
                configuration.locations(
                    locations
                )
            }
        )
        return configuration
    }
}
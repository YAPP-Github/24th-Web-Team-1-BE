package com.few.batch

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.util.function.Consumer
import javax.sql.DataSource

@Configuration
@Import(BatchTestDataSourceConfig::class)
class BatchTestFlywayConfig {
    @Bean
    fun flyway(
        configuration: org.flywaydb.core.api.configuration.Configuration?,
    ): Flyway {
        return Flyway(configuration)
    }

    @Bean
    fun flywayMigrationInitializer(
        flyway: Flyway?,
    ): FlywayMigrationInitializer {
        return FlywayMigrationInitializer(flyway) { obj: Flyway -> obj.migrate() }
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.flyway")
    fun flywayProperties(): FlywayProperties {
        return FlywayProperties()
    }

    @Bean
    fun configuration(
        dataSource: DataSource?,
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
package com.few.data.flyway

import com.few.data.config.DataConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.util.function.Consumer
import javax.sql.DataSource

@Configuration
class FlywayConfig {
    @Bean(name = [DataConfig.BEAN_NAME_PREFIX + "Flyway"])
    fun flyway(
        configuration: org.flywaydb.core.api.configuration.Configuration?
    ): Flyway {
        return Flyway(configuration)
    }

    @Profile("!new")
    @Bean(name = [DataConfig.BEAN_NAME_PREFIX + "FlywayValidateInitializer"])
    fun flywayValidateInitializer(
        flyway: Flyway?
    ): FlywayMigrationInitializer {
        return FlywayMigrationInitializer(flyway) { obj: Flyway -> obj.validate() }
    }

    @Bean(name = [DataConfig.BEAN_NAME_PREFIX + "FlywayMigrationInitializer"])
    fun flywayMigrationInitializer(
        flyway: Flyway?
    ): FlywayMigrationInitializer {
        return FlywayMigrationInitializer(flyway) { obj: Flyway -> obj.migrate() }
    }

    @Bean(name = [DataConfig.BEAN_NAME_PREFIX + "FlywayProperties"])
    @ConfigurationProperties(prefix = "spring.flyway")
    fun flywayProperties(): FlywayProperties {
        return FlywayProperties()
    }

    @Bean(name = [DataConfig.BEAN_NAME_PREFIX + "FlywayConfiguration"])
    fun configuration(
        dataSource: DataSource?
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

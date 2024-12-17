package com.few.api.config

import com.few.api.config.properties.ApiThreadPoolProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class ApiDatabaseAccessThreadPoolConfig {
    private val log = KotlinLogging.logger {}

    companion object {
        const val DATABASE_ACCESS_POOL = "database-task-"
    }

    @Bean
    @ConfigurationProperties(prefix = "thread-pool.database")
    fun databaseAccessThreadPoolProperties(): ApiThreadPoolProperties = ApiThreadPoolProperties()

    @Bean(DATABASE_ACCESS_POOL)
    fun databaseAccessThreadPool() =
        ThreadPoolTaskExecutor().apply {
            val properties = databaseAccessThreadPoolProperties()
            corePoolSize = properties.getCorePoolSize()
            maxPoolSize = properties.getMaxPoolSize()
            queueCapacity = properties.getQueueCapacity()
            setWaitForTasksToCompleteOnShutdown(properties.getWaitForTasksToCompleteOnShutdown())
            setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds())
            setThreadNamePrefix("databaseAccessThreadPool-")
            setRejectedExecutionHandler { r, _ ->
                log.warn { "Database Access Task Rejected: $r" }
            }
            initialize()
        }
}
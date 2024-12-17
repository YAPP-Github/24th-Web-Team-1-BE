package com.few.api.config

import com.few.api.config.properties.ApiThreadPoolProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import web.config.ClonedTaskDecorator

@Configuration
class ApiThreadPoolConfig {
    private val log = KotlinLogging.logger {}

    companion object {
        const val DISCORD_HOOK_EVENT_POOL = "discord-task-"
    }

    @Bean
    @ConfigurationProperties(prefix = "thread-pool.discord")
    fun disCordThreadPoolProperties(): ApiThreadPoolProperties = ApiThreadPoolProperties()

    @Bean(DISCORD_HOOK_EVENT_POOL)
    fun discordHookThreadPool() =
        ThreadPoolTaskExecutor().apply {
            val properties = disCordThreadPoolProperties()
            corePoolSize = properties.getCorePoolSize()
            maxPoolSize = properties.getMaxPoolSize()
            queueCapacity = properties.getQueueCapacity()
            setWaitForTasksToCompleteOnShutdown(properties.getWaitForTasksToCompleteOnShutdown())
            setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds())
            setThreadNamePrefix("discordHookThreadPool-")
            setRejectedExecutionHandler { r, _ ->
                log.warn { "Discord Hook Event Task Rejected: $r" }
            }
            setTaskDecorator(ClonedTaskDecorator())
            initialize()
        }
}
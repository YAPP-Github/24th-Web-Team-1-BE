package com.few.api.config.properties

import java.lang.IllegalStateException

data class ApiThreadPoolProperties(
    var corePoolSize: Int? = null,
    var maxPoolSize: Int? = null,
    var queueCapacity: Int? = null,
    var waitForTasksToCompleteOnShutdown: Boolean? = null,
    var awaitTerminationSeconds: Int? = null,
) {
    fun getCorePoolSize(): Int {
        return corePoolSize ?: throw IllegalStateException("core pool size")
    }

    fun getMaxPoolSize(): Int {
        return maxPoolSize ?: throw IllegalStateException("max pool size")
    }

    fun getQueueCapacity(): Int {
        return queueCapacity ?: throw IllegalStateException("queue capacity")
    }

    fun getWaitForTasksToCompleteOnShutdown(): Boolean {
        return waitForTasksToCompleteOnShutdown ?: throw IllegalStateException("waitForTasksToCompleteOnShutdown")
    }

    fun getAwaitTerminationSeconds(): Int {
        return awaitTerminationSeconds ?: throw IllegalStateException("awaitTerminationSeconds")
    }
}
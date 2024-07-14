package com.few.api.config.properties

import com.few.api.exception.properties.NotSetPropertyException

data class ThreadPoolProperties(
    var corePoolSize: Int? = null,
    var maxPoolSize: Int? = null,
    var queueCapacity: Int? = null,
    var waitForTasksToCompleteOnShutdown: Boolean? = null,
    var awaitTerminationSeconds: Int? = null,
) {
    fun getCorePoolSize(): Int {
        return corePoolSize ?: throw NotSetPropertyException("core pool size")
    }

    fun getMaxPoolSize(): Int {
        return maxPoolSize ?: throw NotSetPropertyException("max pool size")
    }

    fun getQueueCapacity(): Int {
        return queueCapacity ?: throw NotSetPropertyException("queue capacity")
    }

    fun getWaitForTasksToCompleteOnShutdown(): Boolean {
        return waitForTasksToCompleteOnShutdown ?: throw NotSetPropertyException("waitForTasksToCompleteOnShutdown")
    }

    fun getAwaitTerminationSeconds(): Int {
        return awaitTerminationSeconds ?: throw NotSetPropertyException("awaitTerminationSeconds")
    }
}
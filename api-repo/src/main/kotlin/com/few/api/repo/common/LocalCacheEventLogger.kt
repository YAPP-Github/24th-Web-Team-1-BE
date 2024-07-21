package com.few.api.repo.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.ehcache.event.CacheEvent
import org.ehcache.event.CacheEventListener

class LocalCacheEventLogger : CacheEventListener<Any, Any> {
    private val log = KotlinLogging.logger {}

    override fun onEvent(event: CacheEvent<out Any, out Any>) {
        log.debug { "Local Cache event: ${event.type} for item with key: ${event.key}. " }
    }
}
package com.few.api.repo.config

import com.few.api.repo.common.LocalCacheEventLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.ehcache.event.EventType
import org.ehcache.impl.config.event.DefaultCacheEventListenerConfiguration
import org.ehcache.jsr107.Eh107Configuration
import org.ehcache.jsr107.EhcacheCachingProvider
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.jcache.JCacheCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class LocalCacheConfig {
    private val log = KotlinLogging.logger {}

    companion object {
        const val LOCAL_CM = "localCacheManager"
        const val SELECT_ARTICLE_RECORD_CACHE = "selectArticleRecordCache"
        const val SELECT_WORKBOOK_RECORD_CACHE = "selectWorkBookRecordCache"
    }

    @Bean(LOCAL_CM)
    fun localCacheManager(): CacheManager {
        val cacheEventListenerConfigurationConfig = DefaultCacheEventListenerConfiguration(
            setOf(
                EventType.CREATED,
                EventType.EXPIRED,
                EventType.REMOVED,
                EventType.UPDATED
            ),
            LocalCacheEventLogger::class.java
        )
        val cacheManager = EhcacheCachingProvider().cacheManager

        val cacheConfigurationBuilder = CacheConfigurationBuilder.newCacheConfigurationBuilder(
            Any::class.java,
            Any::class.java,
            ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(50, EntryUnit.ENTRIES)
        )
            .withService(cacheEventListenerConfigurationConfig)
            .build()

        val selectArticleRecordCacheConfig: javax.cache.configuration.Configuration<Any, Any> =
            Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfigurationBuilder)
        val selectWorkBookRecordCacheConfig: javax.cache.configuration.Configuration<Any, Any> =
            Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfigurationBuilder)
        runCatching {
            cacheManager.createCache(SELECT_ARTICLE_RECORD_CACHE, selectArticleRecordCacheConfig)
            cacheManager.createCache(SELECT_WORKBOOK_RECORD_CACHE, selectWorkBookRecordCacheConfig)
        }.onFailure {
            log.error(it) { "Failed to create cache" }
        }

        return JCacheCacheManager(cacheManager)
    }
}
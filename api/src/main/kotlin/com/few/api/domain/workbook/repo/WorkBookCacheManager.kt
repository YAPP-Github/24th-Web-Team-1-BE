package com.few.api.domain.workbook.repo

import com.few.api.config.ApiLocalCacheConfig.Companion.SELECT_WORKBOOK_RECORD_CACHE
import com.few.api.domain.workbook.repo.record.SelectWorkBookRecord
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import javax.cache.Cache

@Suppress("UNCHECKED_CAST")
@Service
class WorkBookCacheManager(
    private val cacheManager: CacheManager,
) {
    private var selectWorkBookCache: Cache<Any, Any> = cacheManager.getCache(SELECT_WORKBOOK_RECORD_CACHE)?.nativeCache as Cache<Any, Any>

    fun getAllSelectWorkBookValues(): List<SelectWorkBookRecord> {
        val values = mutableListOf<SelectWorkBookRecord>()
        selectWorkBookCache.iterator().forEach {
            values.add(it.value as SelectWorkBookRecord)
        }
        return values
    }
}
package com.few.api.repo.dao.workbook

import com.few.api.repo.config.LocalCacheConfig.Companion.SELECT_WORKBOOK_RECORD_CACHE
import com.few.api.repo.dao.workbook.record.SelectWorkBookRecord
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import javax.cache.Cache

@Suppress("UNCHECKED_CAST")
@Service
class WorkBookCacheManager(
    private val cacheManager: CacheManager,
) {

    private var selectWorkBookCache: Cache<Any, Any> = cacheManager.getCache(SELECT_WORKBOOK_RECORD_CACHE)?.nativeCache as Cache<Any, Any>

    fun getAllSelectWorkBookKeys(): List<Long> {
        val keys = mutableListOf<Long>()
        selectWorkBookCache.iterator().forEach {
            keys.add(it.key as Long)
        }
        return keys
    }

    fun getAllSelectWorkBookValues(): List<SelectWorkBookRecord> {
        val values = mutableListOf<SelectWorkBookRecord>()
        selectWorkBookCache.iterator().forEach {
            values.add(it.value as SelectWorkBookRecord)
        }
        return values
    }
}
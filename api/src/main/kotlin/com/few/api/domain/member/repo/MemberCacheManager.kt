package com.few.api.domain.member.repo

import com.few.api.config.ApiLocalCacheConfig.Companion.SELECT_WRITER_CACHE
import com.few.api.domain.member.repo.record.WriterRecord
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import javax.cache.Cache

@Suppress("UNCHECKED_CAST")
@Service
class MemberCacheManager(
    private val cacheManager: CacheManager,
) {

    private var selectWriterCache: Cache<Any, Any> = cacheManager.getCache(SELECT_WRITER_CACHE)?.nativeCache as Cache<Any, Any>

    fun getAllWriterValues(): List<WriterRecord> {
        val values = mutableListOf<WriterRecord>()
        selectWriterCache.iterator().forEach {
            values.add(it.value as WriterRecord)
        }
        return values
    }

    fun getAllWriterValues(keys: List<Long>): List<WriterRecord> {
        val values = mutableListOf<WriterRecord>()
        keys.forEach {
            selectWriterCache.get(it)?.let { value ->
                values.add(value as WriterRecord)
            }
        }
        return values
    }

    fun addSelectWorkBookCache(records: List<WriterRecord>) {
        records.forEach {
            selectWriterCache.put(it.writerId, it)
        }
    }
}
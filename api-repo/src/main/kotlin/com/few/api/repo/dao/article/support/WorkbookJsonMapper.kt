package com.few.api.repo.dao.article.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.few.api.repo.dao.article.record.WorkbookRecord
import org.springframework.stereotype.Component

@Component
class WorkbookJsonMapper(
    private val objectMapper: ObjectMapper,
) {

    fun toObject(jsonStr: String): List<WorkbookRecord> {
        return objectMapper.readValue(jsonStr)
    }

    fun toJsonStr(workBooks: List<WorkbookRecord>): String {
        return objectMapper.writeValueAsString(workBooks)
    }
}
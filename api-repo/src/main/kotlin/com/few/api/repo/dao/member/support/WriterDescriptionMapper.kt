package com.few.api.repo.dao.member.support

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class WriterDescriptionMapper(
    private val objectMapper: ObjectMapper
) {
    fun toJson(writerDescription: WriterDescription): String {
        return objectMapper.writeValueAsString(writerDescription)
    }

    fun toObject(value: String): WriterDescription {
        return objectMapper.readValue(value, WriterDescription::class.java)
    }
}
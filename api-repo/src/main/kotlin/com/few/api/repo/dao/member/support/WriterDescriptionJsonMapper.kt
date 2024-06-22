package com.few.api.repo.dao.member.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.stereotype.Component

@Component
class WriterDescriptionJsonMapper(
    private val objectMapper: ObjectMapper
) {
    init {
        objectMapper.registerKotlinModule()
    }

    fun toJson(writerDescription: WriterDescription): String {
        return objectMapper.writeValueAsString(writerDescription)
    }

    fun toObject(value: String): WriterDescription {
        return objectMapper.readValue(value, WriterDescription::class.java)
    }
}
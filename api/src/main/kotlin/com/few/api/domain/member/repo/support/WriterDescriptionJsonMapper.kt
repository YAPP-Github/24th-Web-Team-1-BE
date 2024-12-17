package com.few.api.domain.member.repo.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.stereotype.Component

@Component
class WriterDescriptionJsonMapper(
    private val objectMapper: ObjectMapper,
) {
    init {
        objectMapper.registerKotlinModule()
    }

    fun toJson(writerDescription: WriterDescription): String = objectMapper.writeValueAsString(writerDescription)

    fun toObject(value: String): WriterDescription = objectMapper.readValue(value, WriterDescription::class.java)
}
package com.few.api.domain.problem.repo.support

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class ContentsJsonMapper(
    private val objectMapper: ObjectMapper,
) {
    fun toJson(contents: Contents): String = objectMapper.writeValueAsString(contents)

    fun toObject(value: String): Contents {
        val contents = objectMapper.readTree(value).get("contents")
        val contentList = mutableListOf<Content>()
        contents.forEach {
            contentList.add(Content(it.get("number").asLong(), it.get("content").asText()))
        }
        return Contents(contentList)
    }
}
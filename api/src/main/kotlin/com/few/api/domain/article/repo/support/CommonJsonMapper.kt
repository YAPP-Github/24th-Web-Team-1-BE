package com.few.api.domain.article.repo.support

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class CommonJsonMapper( // TODO: common 성 패키지 위치로 이동
    private val objectMapper: ObjectMapper,
) {
    fun toJsonStr(map: Map<String, Any>): String {
        return objectMapper.writeValueAsString(map)
    }
}
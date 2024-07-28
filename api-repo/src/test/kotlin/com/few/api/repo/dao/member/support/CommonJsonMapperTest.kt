package com.few.api.repo.dao.member.support

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

import java.net.URL

class CommonJsonMapperTest {

    private var writerDescriptionJsonMapper: WriterDescriptionJsonMapper = WriterDescriptionJsonMapper(ObjectMapper())

    @Test
    fun `WriterDescription을 Json 형식으로 변환합니다`() {
        // Given
        val writerDescription = WriterDescription(
            name = "writer",
            url = URL("http://localhost:8080/writers/url")
        )

        // When
        val json = writerDescriptionJsonMapper.toJson(writerDescription)

        // Then
        assertNotNull(json)
        assertTrue(json.isNotBlank())
        assertTrue(json.contains("writer"))
        assertTrue(json.contains("http://localhost:8080/writers/url"))
    }

    @Test
    fun `Json 형식의 WriterDescription을 WriterDescription으로 변환합니다`() {
        // Given
        val json = """
            {
                "name": "writer",
                "url": "http://localhost:8080/writers/url"
            }
        """.trimIndent()

        // When
        val writerDescription = writerDescriptionJsonMapper.toObject(json)

        // Then
        assertNotNull(writerDescription)
        assertEquals("writer", writerDescription.name)
        assertEquals(URL("http://localhost:8080/writers/url"), writerDescription.url)
    }
}
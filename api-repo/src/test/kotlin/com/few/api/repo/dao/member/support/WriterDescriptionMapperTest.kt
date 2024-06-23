package com.few.api.repo.dao.member.support

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import java.net.URL

@SpringBootTest(classes = [ObjectMapper::class])
class WriterDescriptionMapperTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper
    private lateinit var writerDescriptionMapper: WriterDescriptionMapper

    @BeforeEach
    fun setUp() {
        writerDescriptionMapper = WriterDescriptionMapper(objectMapper)
    }

    @Test
    fun `WriterDescription을 Json 형식으로 변환합니다`() {
        // Given
        val writerDescription = WriterDescription(
            name = "writer",
            url = URL("http://localhost:8080/writers/url")
        )

        // When
        val json = writerDescriptionMapper.toJson(writerDescription)

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
        val writerDescription = writerDescriptionMapper.toObject(json)

        // Then
        assertNotNull(writerDescription)
        assertEquals("writer", writerDescription.name)
        assertEquals(URL("http://localhost:8080/writers/url"), writerDescription.url)
    }
}
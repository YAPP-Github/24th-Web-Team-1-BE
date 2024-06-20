package com.few.api.repo.dao.problem.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.repo.config.ApiRepoObjectMapperConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [ApiRepoObjectMapperConfig::class])
class ContentsMapperTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var contentsMapper: ContentsMapper

    @BeforeEach
    fun setUp() {
        contentsMapper = ContentsMapper(objectMapper)
    }

    @Test
    fun `Contents를 Json 형식으로 변환합니다`() {
        // Given
        val contents = Contents(
            contents = listOf(
                Content(1L, "this is number one"),
                Content(2L, "this is number two")
            )
        )

        // When
        val json = contentsMapper.toJson(contents)

        // Then
        assertNotNull(json)
        assertTrue(json.isNotBlank())
        assertTrue(json.contains("this is number one"))
        assertTrue(json.contains("this is number two"))
    }

    @Test
    fun `Json 형식의 Contents를 Contents으로 변환합니다`() {
        // Given
        val json = """
            {
                "contents": [
                    {
                        "id": 1,
                        "content": "this is number one"
                    },
                    {
                        "id": 2,
                        "content": "this is number two"
                    }
                ]
            }
        """.trimIndent()

        // When
        val contents = contentsMapper.toObject(json)

        // Then
        assertNotNull(contents)
        assertEquals(2, contents.contents.size)
        assertEquals(1L, contents.contents[0].number)
        assertEquals("this is number one", contents.contents[0].content)
        assertEquals(2L, contents.contents[1].number)
        assertEquals("this is number two", contents.contents[1].content)
    }
}
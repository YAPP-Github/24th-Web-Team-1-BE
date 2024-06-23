package com.few.batch.log

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.batch.BatchTestSpec
import jooq.jooq_dsl.Tables.BATCH_CALL_EXECUTION
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BatchCallExecutionServiceTest : BatchTestSpec() {

    @Autowired
    private lateinit var dslContext: DSLContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var batchCallExecutionService: BatchCallExecutionService

    @BeforeEach
    fun setUp() {
        dslContext.deleteFrom(BATCH_CALL_EXECUTION).execute()
    }

    @Test
    fun `배치 결과가 성공만 있는 경우`() {
        // given
        val status = true
        val descriptionSource = mapOf("success" to listOf(1, 2, 3))
        val description = objectMapper.writeValueAsString(descriptionSource)

        // when
        batchCallExecutionService.execute(status, description)

        // then
        val result = dslContext.select(BATCH_CALL_EXECUTION.STATUS, BATCH_CALL_EXECUTION.DESCRIPTION)
            .from(BATCH_CALL_EXECUTION)
            .fetch()

        assertEquals(1, result.size)
        assertEquals(status, result[0].value1())
        objectMapper.readTree(description).let { expected ->
            objectMapper.readTree(result[0].value2().data()).let { actual ->
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun `배치 결과가 실패만 있는 경우`() {
        // given
        val status = false
        val descriptionSource = mapOf("fail" to mapOf("EmailSendFail" to listOf(1, 2, 3)))
        val description = objectMapper.writeValueAsString(descriptionSource)

        // when
        batchCallExecutionService.execute(status, description)

        // then
        val result = dslContext.select(BATCH_CALL_EXECUTION.STATUS, BATCH_CALL_EXECUTION.DESCRIPTION)
            .from(BATCH_CALL_EXECUTION)
            .fetch()

        assertEquals(1, result.size)
        assertEquals(status, result[0].value1())
        objectMapper.readTree(description).let { expected ->
            objectMapper.readTree(result[0].value2().data()).let { actual ->
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun `배치 결과가 성공과 실패가 섞여 있는 경우`() {
        // given
        val status = false
        val descriptionSource = mapOf("success" to listOf(1, 2, 3), "fail" to mapOf("EmailSendFail" to listOf(4, 5, 6)))
        val description = objectMapper.writeValueAsString(descriptionSource)

        // when
        batchCallExecutionService.execute(status, description)

        // then
        val result = dslContext.select(BATCH_CALL_EXECUTION.STATUS, BATCH_CALL_EXECUTION.DESCRIPTION)
            .from(BATCH_CALL_EXECUTION)
            .fetch()

        assertEquals(1, result.size)
        assertEquals(status, result[0].value1())
        objectMapper.readTree(description).let { expected ->
            objectMapper.readTree(result[0].value2().data()).let { actual ->
                assertEquals(expected, actual)
            }
        }
    }
}
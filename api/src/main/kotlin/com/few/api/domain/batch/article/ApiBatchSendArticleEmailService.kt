package com.few.api.domain.batch.article

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.batch.article.reader.WorkBookSubscriberReader
import com.few.api.domain.batch.article.writer.WorkBookSubscriberWriter
import com.few.api.domain.batch.log.ApiBatchCallExecutionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ApiBatchSendArticleEmailService(
    private val workBookSubscriberReader: WorkBookSubscriberReader,
    private val workBookSubscriberWriter: WorkBookSubscriberWriter,
    private val batchCallExecutionService: ApiBatchCallExecutionService,
    private val objectMapper: ObjectMapper,
) {

    @Transactional
    fun execute() {
        val startTime = System.currentTimeMillis()
        workBookSubscriberReader.execute().let { item ->
            workBookSubscriberWriter.execute(item).let { resultExecution ->
                val elapsedTime = System.currentTimeMillis() - startTime
                resultExecution.plus("elapsedTime" to elapsedTime).let { execution ->
                    objectMapper.writeValueAsString(execution).let { json ->
                        if (!json.contains("fail")) {
                            batchCallExecutionService.execute(true, json)
                        } else {
                            batchCallExecutionService.execute(false, json)
                        }
                    }
                }
            }
        }
    }
}
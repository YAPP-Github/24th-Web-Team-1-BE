package com.few.batch.service.article

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.batch.log.BatchCallExecutionService
import com.few.batch.service.article.reader.WorkBookSubscriberReader
import com.few.batch.service.article.writer.WorkBookSubscriberWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BatchSendArticleEmailService(
    private val workBookSubscriberReader: WorkBookSubscriberReader,
    private val workBookSubscriberWriter: WorkBookSubscriberWriter,
    private val batchCallExecutionService: BatchCallExecutionService,
    private val objectMapper: ObjectMapper
) {
    @Transactional
    fun execute() {
        workBookSubscriberReader.execute().let { item ->
            workBookSubscriberWriter.execute(item).let { execution ->
                objectMapper.writeValueAsString(execution).let { json ->
                    if (!json.contains("fail")) {
                        batchCallExecutionService.execute(false, json)
                    } else {
                        batchCallExecutionService.execute(true, json)
                    }
                }
            }
        }
    }
}
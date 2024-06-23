package com.few.batch.service.article

import com.few.batch.service.article.reader.WorkBookSubscriberReader
import com.few.batch.service.article.writer.WorkBookSubscriberWriter
import org.springframework.stereotype.Service

@Service
class BatchSendArticleEmailService(
    private val workBookSubscriberReader: WorkBookSubscriberReader,
    private val workBookSubscriberWriter: WorkBookSubscriberWriter
) {
    fun execute() {
        workBookSubscriberReader.execute().let { item ->
            workBookSubscriberWriter.execute(item)
        }
    }
}
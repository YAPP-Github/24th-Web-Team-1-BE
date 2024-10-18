package com.few.batch.service.article

import com.few.batch.service.article.reader.WeeklyArticleIdReader
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BatchSendWeeklyArticleEmailService(
    private val weeklyArticleIdReader: WeeklyArticleIdReader,
) {
    private val log = KotlinLogging.logger {}

    @Transactional
    fun execute() {
        val articleIds = weeklyArticleIdReader.browseWeeklyArticleIds()

        log.debug { "Read Weekly TOP5 Article IDs: $articleIds " }
    }
}
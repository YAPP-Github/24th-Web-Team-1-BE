package com.few.api.domain.article.handler

import com.few.api.config.DatabaseAccessThreadPoolConfig.Companion.DATABASE_ACCESS_POOL
import com.few.api.repo.dao.article.ArticleViewHisDao
import com.few.api.repo.dao.article.command.ArticleViewHisCommand
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ArticleViewHisAsyncHandler(
    private val articleViewHisDao: ArticleViewHisDao,
) {
    private val log = KotlinLogging.logger {}

    @Async(value = DATABASE_ACCESS_POOL)
    @Transactional
    fun addArticleViewHis(articleId: Long, memberId: Long) {
        try {
            articleViewHisDao.insertArticleViewHis(ArticleViewHisCommand(articleId, memberId))
            log.debug { "Successfully inserted article view history for articleId: $articleId and memberId: $memberId" }
        } catch (e: Exception) {
            log.error { "Failed to insert article view history for articleId: $articleId and memberId: $memberId" }
        }
    }
}
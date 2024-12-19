package com.few.api.domain.article.event.handler

import com.few.api.config.ApiDatabaseAccessThreadPoolConfig.Companion.DATABASE_ACCESS_POOL
import com.few.api.domain.article.repo.ArticleViewCountDao
import com.few.api.domain.article.repo.ArticleViewHisDao
import com.few.api.domain.article.repo.command.ArticleViewHisCommand
import com.few.api.domain.article.repo.query.ArticleViewCountQuery
import com.few.api.domain.common.vo.CategoryType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import repo.jooq.DataSourceTransactional

@Component
class ArticleViewHisAsyncHandler(
    private val articleViewHisDao: ArticleViewHisDao,
    private val articleViewCountDao: ArticleViewCountDao,
) {
    private val log = KotlinLogging.logger {}

    @Async(value = DATABASE_ACCESS_POOL)
    @DataSourceTransactional
    fun addArticleViewHis(
        articleId: Long,
        memberId: Long,
        categoryType: CategoryType,
    ) {
        runCatching {
            articleViewHisDao
                .insertArticleViewHis(
                    ArticleViewHisCommand(
                        articleId,
                        memberId,
                    ),
                ).also {
                    log.debug { "Successfully inserted article view history for articleId: $articleId and memberId: $memberId" }
                }

            articleViewCountDao.upsertArticleViewCount(ArticleViewCountQuery(articleId, categoryType)).also {
                log.debug { "Successfully upserted article view count for articleId: $articleId and categoryType: $categoryType" }
            }
        }.onFailure { e ->
            log.error(e) {
                "Failed insertion article view history and upsertion article view count " +
                    "for articleId: $articleId and memberId: $memberId"
            }
        }.getOrThrow()
    }
}
package com.few.api.domain.article.usecase

import com.few.api.config.DatabaseAccessThreadPoolConfig.Companion.DATABASE_ACCESS_POOL
import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseIn
import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseOut
import com.few.api.domain.article.usecase.dto.WriterDetail
import com.few.api.domain.article.service.BrowseArticleProblemsService
import com.few.api.domain.article.service.ReadArticleWriterRecordService
import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsInDto
import com.few.api.domain.article.service.dto.ReadWriterRecordInDto
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.ArticleViewHisDao
import com.few.api.repo.dao.article.command.ArticleViewHisCommand
import com.few.api.repo.dao.article.query.ArticleViewHisCountQuery
import com.few.api.repo.dao.article.query.SelectArticleRecordQuery
import com.few.data.common.code.CategoryType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReadArticleUseCase(
    private val articleDao: ArticleDao,
    private val readArticleWriterRecordService: ReadArticleWriterRecordService,
    private val browseArticleProblemsService: BrowseArticleProblemsService,
    private val articleViewHisDao: ArticleViewHisDao,
) {

    private val log = KotlinLogging.logger {}

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadArticleUseCaseIn): ReadArticleUseCaseOut {
        val articleRecord = SelectArticleRecordQuery(useCaseIn.articleId).let { query: SelectArticleRecordQuery ->
            articleDao.selectArticleRecord(query)
        } ?: throw NotFoundException("article.notfound.id")

        val writerRecord = ReadWriterRecordInDto(articleRecord.writerId).let { query: ReadWriterRecordInDto ->
            readArticleWriterRecordService.execute(query) ?: throw NotFoundException("writer.notfound.id")
        }

        val problemIds =
            BrowseArticleProblemIdsInDto(articleRecord.articleId).let { query: BrowseArticleProblemIdsInDto ->
                browseArticleProblemsService.execute(query)
            }

        val views = (articleViewHisDao.countArticleViews(ArticleViewHisCountQuery(useCaseIn.articleId)) ?: 0L) + 1L

        insertArticleViewHisAsync(useCaseIn.articleId, useCaseIn.memberId)

        return ReadArticleUseCaseOut(
            id = articleRecord.articleId,
            writer = WriterDetail(
                id = writerRecord.writerId,
                name = writerRecord.name,
                url = writerRecord.url
            ),
            title = articleRecord.title,
            content = articleRecord.content,
            problemIds = problemIds.problemIds,
            category = CategoryType.convertToDisplayName(articleRecord.category),
            createdAt = articleRecord.createdAt,
            views = views
        )
    }

    @Async(value = DATABASE_ACCESS_POOL)
    @Transactional
    fun insertArticleViewHisAsync(articleId: Long, memberId: Long) {
        try {
            articleViewHisDao.insertArticleViewHis(ArticleViewHisCommand(articleId, memberId))
            log.debug { "Successfully inserted article view history for articleId: $articleId and memberId: $memberId" }
        } catch (e: Exception) {
            log.error { "Failed to insert article view history for articleId: $articleId and memberId: $memberId" }
        }
    }
}
package com.few.api.domain.workbook.article.usecase

import com.few.api.domain.article.event.dto.ReadArticleEvent
import com.few.api.domain.article.handler.ArticleViewCountHandler
import com.few.api.domain.article.service.BrowseArticleProblemsService
import com.few.api.domain.article.service.ReadArticleWriterRecordService
import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsInDto
import com.few.api.domain.article.service.dto.ReadWriterRecordInDto
import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleUseCaseIn
import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleOut
import com.few.api.domain.workbook.article.dto.WriterDetail
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.query.SelectWorkBookArticleRecordQuery
import com.few.data.common.code.CategoryType
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReadWorkBookArticleUseCase(
    private val articleDao: ArticleDao,
    private val readArticleWriterRecordService: ReadArticleWriterRecordService,
    private val browseArticleProblemsService: BrowseArticleProblemsService,
    private val articleViewCountHandler: ArticleViewCountHandler,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadWorkBookArticleUseCaseIn): ReadWorkBookArticleOut {
        val articleRecord = SelectWorkBookArticleRecordQuery(
            useCaseIn.workbookId,
            useCaseIn.articleId
        ).let { query: SelectWorkBookArticleRecordQuery ->
            articleDao.selectWorkBookArticleRecord(query)
        } ?: throw NotFoundException("article.notfound.articleidworkbookid")

        val writerRecord =
            ReadWriterRecordInDto(articleRecord.writerId).let { query: ReadWriterRecordInDto ->
                readArticleWriterRecordService.execute(query) ?: throw NotFoundException("writer.notfound.id")
            }

        val problemIds =
            BrowseArticleProblemIdsInDto(articleRecord.articleId).let { query: BrowseArticleProblemIdsInDto ->
                browseArticleProblemsService.execute(query)
            }

        /**
         * @see com.few.api.domain.article.usecase.ReadArticleUseCase
         */
        articleViewCountHandler.browseArticleViewCount(useCaseIn.articleId)
        applicationEventPublisher.publishEvent(
            ReadArticleEvent(
                articleId = useCaseIn.articleId,
                memberId = useCaseIn.memberId,
                category = CategoryType.fromCode(articleRecord.category) ?: throw NotFoundException(
                    "article.invalid.category"
                )
            )
        )

        return ReadWorkBookArticleOut(
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
            day = articleRecord.day
        )
    }
}
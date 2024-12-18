package com.few.api.domain.workbook.article.usecase

import com.few.api.domain.article.event.dto.ReadArticleEvent
import com.few.api.domain.article.repo.ArticleDao
import com.few.api.domain.article.repo.query.SelectWorkBookArticleRecordQuery
import com.few.api.domain.article.service.BrowseArticleProblemsService
import com.few.api.domain.article.service.ReadArticleWriterRecordService
import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsInDto
import com.few.api.domain.article.service.dto.ReadWriterRecordInDto
import com.few.api.domain.article.usecase.transaction.ArticleViewCountTxCase
import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.common.vo.CategoryType
import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleOut
import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleUseCaseIn
import com.few.api.domain.workbook.article.dto.WriterDetail
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import repo.jooq.DataSourceTransactional

@Service
class ReadWorkBookArticleUseCase(
    private val articleDao: ArticleDao,
    private val readArticleWriterRecordService: ReadArticleWriterRecordService,
    private val browseArticleProblemsService: BrowseArticleProblemsService,
    private val articleViewCountTxCase: ArticleViewCountTxCase,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @DataSourceTransactional(readOnly = true)
    fun execute(useCaseIn: ReadWorkBookArticleUseCaseIn): ReadWorkBookArticleOut {
        val articleRecord =
            articleDao.selectWorkBookArticleRecord(
                SelectWorkBookArticleRecordQuery(
                    useCaseIn.workbookId,
                    useCaseIn.articleId,
                ),
            ) ?: throw NotFoundException("article.notfound.articleidworkbookid")

        val writerRecord =
            readArticleWriterRecordService.execute(ReadWriterRecordInDto(articleRecord.writerId))
                ?: throw NotFoundException("writer.notfound.id")

        val problemIds =
            browseArticleProblemsService.execute(BrowseArticleProblemIdsInDto(articleRecord.articleId))

        articleViewCountTxCase.browseArticleViewCount(useCaseIn.articleId)
        applicationEventPublisher.publishEvent(
            ReadArticleEvent(
                articleId = useCaseIn.articleId,
                memberId = useCaseIn.memberId,
                category =
                    CategoryType.fromCode(articleRecord.category) ?: throw NotFoundException(
                        "article.invalid.category",
                    ),
            ),
        )

        return ReadWorkBookArticleOut(
            id = articleRecord.articleId,
            writer =
                WriterDetail(
                    id = writerRecord.writerId,
                    name = writerRecord.name,
                    url = writerRecord.url,
                ),
            title = articleRecord.title,
            content = articleRecord.content,
            problemIds = problemIds.problemIds,
            category = CategoryType.convertToDisplayName(articleRecord.category),
            createdAt = articleRecord.createdAt,
            day = articleRecord.day,
        )
    }
}
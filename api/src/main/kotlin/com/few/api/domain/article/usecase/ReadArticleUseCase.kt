package com.few.api.domain.article.usecase

import com.few.api.domain.article.event.dto.ReadArticleEvent
import com.few.api.domain.article.repo.ArticleDao
import com.few.api.domain.article.repo.query.SelectArticleRecordQuery
import com.few.api.domain.article.service.BrowseArticleProblemsService
import com.few.api.domain.article.service.ReadArticleWriterRecordService
import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsInDto
import com.few.api.domain.article.service.dto.ReadWriterRecordInDto
import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseIn
import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseOut
import com.few.api.domain.article.usecase.dto.WriterDetail
import com.few.api.domain.article.usecase.transaction.ArticleViewCountTxCase
import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.common.vo.CategoryType
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReadArticleUseCase(
    private val articleDao: ArticleDao,
    private val readArticleWriterRecordService: ReadArticleWriterRecordService,
    private val browseArticleProblemsService: BrowseArticleProblemsService,
    private val articleViewCountTxCase: ArticleViewCountTxCase,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadArticleUseCaseIn): ReadArticleUseCaseOut {
        val articleRecord =
            articleDao.selectArticleRecord(SelectArticleRecordQuery(useCaseIn.articleId))
                ?: throw NotFoundException("article.notfound.id")

        val writerRecord =
            readArticleWriterRecordService.execute(ReadWriterRecordInDto(articleRecord.writerId))
                ?: throw NotFoundException("writer.notfound.id")

        val problemIds =
            browseArticleProblemsService.execute(BrowseArticleProblemIdsInDto(articleRecord.articleId))

        val views = articleViewCountTxCase.browseArticleViewCount(useCaseIn.articleId)
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

        return ReadArticleUseCaseOut(
            id = articleRecord.articleId,
            writer =
                WriterDetail(
                    id = writerRecord.writerId,
                    name = writerRecord.name,
                    url = writerRecord.url,
                    imageUrl = writerRecord.imageUrl,
                ),
            mainImageUrl = articleRecord.mainImageURL,
            title = articleRecord.title,
            content = articleRecord.content,
            problemIds = problemIds.problemIds,
            category = CategoryType.convertToDisplayName(articleRecord.category),
            createdAt = articleRecord.createdAt,
            views = views,
        )
    }
}
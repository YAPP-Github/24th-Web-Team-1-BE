package com.few.api.domain.article.usecase

import com.few.api.domain.article.service.ArticleViewHisService
import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseIn
import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseOut
import com.few.api.domain.article.usecase.dto.WriterDetail
import com.few.api.domain.article.service.BrowseArticleProblemsService
import com.few.api.domain.article.service.ReadArticleWriterRecordService
import com.few.api.domain.article.service.dto.AddArticleViewHisInDto
import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsInDto
import com.few.api.domain.article.service.dto.ReadArticleViewsInDto
import com.few.api.domain.article.service.dto.ReadWriterRecordInDto
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.query.SelectArticleRecordQuery
import com.few.data.common.code.CategoryType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReadArticleUseCase(
    private val articleDao: ArticleDao,
    private val readArticleWriterRecordService: ReadArticleWriterRecordService,
    private val browseArticleProblemsService: BrowseArticleProblemsService,
    private val articleViewHisService: ArticleViewHisService,
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadArticleUseCaseIn): ReadArticleUseCaseOut {
        val articleRecord = SelectArticleRecordQuery(useCaseIn.articleId).let { query: SelectArticleRecordQuery ->
            articleDao.selectArticleRecord(query)
        } ?: throw NotFoundException("article.notfound.id")

        val writerRecord = ReadWriterRecordInDto(articleRecord.writerId).let { query: ReadWriterRecordInDto ->
            readArticleWriterRecordService.execute(query) ?: throw NotFoundException("writer.notfound.id")
        }

        val problemIds = BrowseArticleProblemIdsInDto(articleRecord.articleId).let { query: BrowseArticleProblemIdsInDto ->
            browseArticleProblemsService.execute(query)
        }

        articleViewHisService.addArticleViewHis(AddArticleViewHisInDto(useCaseIn.articleId, useCaseIn.memberId))
        val views = articleViewHisService.readArticleViews(ReadArticleViewsInDto(useCaseIn.articleId))

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
}
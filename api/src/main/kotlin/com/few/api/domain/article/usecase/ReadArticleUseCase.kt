package com.few.api.domain.article.usecase

import com.few.api.domain.article.handler.ArticleViewCountHandler
import com.few.api.domain.article.handler.ArticleViewHisAsyncHandler
import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseIn
import com.few.api.domain.article.usecase.dto.ReadArticleUseCaseOut
import com.few.api.domain.article.usecase.dto.WriterDetail
import com.few.api.domain.article.service.BrowseArticleProblemsService
import com.few.api.domain.article.service.ReadArticleWriterRecordService
import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsInDto
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
    private val articleViewHisAsyncHandler: ArticleViewHisAsyncHandler,
    private val articleViewCountHandler: ArticleViewCountHandler,
) {

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

        // ARTICLE VIEW HIS에 저장하기 전에 먼저 VIEW COUNT 조회하는 순서 변경 금지
        val views = articleViewCountHandler.browseArticleViewCount(useCaseIn.articleId)
        articleViewHisAsyncHandler.addArticleViewHis(
            useCaseIn.articleId,
            useCaseIn.memberId,
            CategoryType.fromCode(articleRecord.category) ?: throw NotFoundException("article.invalid.category")
        )

        /**
         * NOTE: The articleViewHisAsyncHandler creates a new transaction that is separate from the current context.
         * So this section, the logic after the articleViewHisAsyncHandler call,
         * is where the mismatch between the two transactions can occur if an exception is thrown.
         */

        return ReadArticleUseCaseOut(
            id = articleRecord.articleId,
            writer = WriterDetail(
                id = writerRecord.writerId,
                name = writerRecord.name,
                url = writerRecord.url,
                imageUrl = writerRecord.imageUrl
            ),
            mainImageUrl = articleRecord.mainImageURL,
            title = articleRecord.title,
            content = articleRecord.content,
            problemIds = problemIds.problemIds,
            category = CategoryType.convertToDisplayName(articleRecord.category),
            createdAt = articleRecord.createdAt,
            views = views
        )
    }
}
package com.few.api.domain.workbook.article.usecase

import com.few.api.domain.article.service.BrowseArticleProblemsService
import com.few.api.domain.article.service.ReadArticleWriterRecordService
import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsQuery
import com.few.api.domain.article.service.dto.ReadWriterRecordQuery
import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleUseCaseIn
import com.few.api.domain.workbook.article.dto.ReadWorkBookArticleOut
import com.few.api.domain.workbook.article.dto.WriterDetail
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.query.SelectWorkBookArticleRecordQuery
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReadWorkBookArticleUseCase(
    private val articleDao: ArticleDao,
    private val readArticleWriterRecordService: ReadArticleWriterRecordService,
    private val browseArticleProblemsService: BrowseArticleProblemsService
) {
    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadWorkBookArticleUseCaseIn): ReadWorkBookArticleOut {
        val articleRecord = SelectWorkBookArticleRecordQuery(
            useCaseIn.workbookId,
            useCaseIn.articleId
        ).let { query: SelectWorkBookArticleRecordQuery ->
            articleDao.selectWorkBookArticleRecord(query)
        } ?: throw IllegalArgumentException("cannot find $useCaseIn.workbookId article record by articleId: $useCaseIn.articleId")

        val writerRecord =
            ReadWriterRecordQuery(articleRecord.writerId).let { query: ReadWriterRecordQuery ->
                readArticleWriterRecordService.execute(query)
            }

        val problemIds =
            BrowseArticleProblemIdsQuery(articleRecord.articleId).let { query: BrowseArticleProblemIdsQuery ->
                browseArticleProblemsService.execute(query)
            }

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
            category = articleRecord.category.toString(), // todo fix to enum
            createdAt = articleRecord.createdAt,
            day = articleRecord.day
        )
    }
}
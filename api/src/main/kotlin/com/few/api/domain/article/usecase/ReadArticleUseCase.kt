package com.few.api.domain.article.usecase

import com.few.api.domain.article.dto.ReadArticleUseCaseIn
import com.few.api.domain.article.dto.ReadArticleUseCaseOut
import com.few.api.domain.article.dto.WriterDetail
import com.few.api.domain.article.service.BrowseArticleProblemsService
import com.few.api.domain.article.service.ReadWriterRecordService
import com.few.api.domain.article.service.dto.BrowseArticleProblemIdsQuery
import com.few.api.domain.article.service.dto.ReadWriterRecordQuery
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.query.SelectArticleRecordQuery
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReadArticleUseCase(
    private val articleDao: ArticleDao,
    private val readWriterRecordService: ReadWriterRecordService,
    private val browseArticleProblemsService: BrowseArticleProblemsService
) {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadArticleUseCaseIn): ReadArticleUseCaseOut {
        val articleRecord = SelectArticleRecordQuery(useCaseIn.articleId).let { query: SelectArticleRecordQuery ->
            articleDao.selectArticleRecord(query)
        }

        val writerRecord = ReadWriterRecordQuery(articleRecord.writerId).let { query: ReadWriterRecordQuery ->
            readWriterRecordService.execute(query)
        }

        val problemIds = BrowseArticleProblemIdsQuery(articleRecord.articleId).let { query: BrowseArticleProblemIdsQuery ->
            browseArticleProblemsService.execute(query)
        }

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
            category = articleRecord.category.toString(), // todo fix to enum
            createdAt = articleRecord.createdAt
        )
    }
}
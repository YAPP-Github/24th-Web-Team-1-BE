package com.few.api.domain.workbook.service

import com.few.api.domain.workbook.service.dto.BrowseWorkbookArticlesInDto
import com.few.api.domain.workbook.service.dto.WorkBookArticleOutDto
import com.few.api.domain.article.repo.ArticleDao
import com.few.api.domain.article.repo.query.SelectWorkbookMappedArticleRecordsQuery
import com.few.api.domain.workbook.service.dto.ArticleDetailOutDto
import org.springframework.stereotype.Service

fun List<WorkBookArticleOutDto>.writerIds(): List<Long> {
    return this.map { it.writerId }
}

fun List<WorkBookArticleOutDto>.toArticleDetails(): List<ArticleDetailOutDto> {
    return this.map { ArticleDetailOutDto(it.articleId, it.title) }
}

@Service
class WorkbookArticleService(
    private val articleDao: ArticleDao,
) {
    fun browseWorkbookArticles(query: BrowseWorkbookArticlesInDto): List<WorkBookArticleOutDto> {
        return articleDao.selectWorkbookMappedArticleRecords(
            SelectWorkbookMappedArticleRecordsQuery(
                query.workbookId
            )
        ).map { record ->
            WorkBookArticleOutDto(
                articleId = record.articleId,
                writerId = record.writerId,
                mainImageURL = record.mainImageURL,
                title = record.title,
                category = record.category,
                content = record.content,
                createdAt = record.createdAt
            )
        }
    }
}
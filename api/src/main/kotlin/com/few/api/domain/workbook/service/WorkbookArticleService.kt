package com.few.api.domain.workbook.service

import com.few.api.domain.workbook.usecase.dto.ArticleDetail
import com.few.api.domain.workbook.service.dto.BrowseWorkbookArticlesInDto
import com.few.api.domain.workbook.service.dto.WorkBookArticleOutDto
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.query.SelectWorkbookMappedArticleRecordsQuery
import org.springframework.stereotype.Service

fun List<WorkBookArticleOutDto>.writerIds(): List<Long> {
    return this.map { it.writerId }
}

fun List<WorkBookArticleOutDto>.toArticleDetails(): List<ArticleDetail> {
    return this.map { ArticleDetail(it.articleId, it.title) }
}

@Service
class WorkbookArticleService(
    private val articleDao: ArticleDao
) {
    fun browseWorkbookArticles(query: BrowseWorkbookArticlesInDto): List<WorkBookArticleOutDto> {
        return SelectWorkbookMappedArticleRecordsQuery(query.workbookId).let { query ->
            articleDao.selectWorkbookMappedArticleRecords(query).map { record ->
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
}
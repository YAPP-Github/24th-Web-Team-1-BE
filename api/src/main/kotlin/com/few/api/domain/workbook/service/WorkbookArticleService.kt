package com.few.api.domain.workbook.service

import com.few.api.domain.workbook.usecase.dto.ArticleDetail
import com.few.api.domain.workbook.service.dto.BrowseWorkbookArticlesQuery
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.query.SelectWorkbookMappedArticleRecordsQuery
import com.few.api.repo.dao.article.record.SelectWorkBookMappedArticleRecord
import org.springframework.stereotype.Service

fun List<SelectWorkBookMappedArticleRecord>.writerIds(): List<Long> {
    return this.map { it.writerId }
}

fun List<SelectWorkBookMappedArticleRecord>.toArticleDetails(): List<ArticleDetail> {
    return this.map { ArticleDetail(it.articleId, it.title) }
}

@Service
class WorkbookArticleService(
    private val articleDao: ArticleDao
) {
    fun browseWorkbookArticles(query: BrowseWorkbookArticlesQuery): List<SelectWorkBookMappedArticleRecord> {
        return SelectWorkbookMappedArticleRecordsQuery(query.workbookId).let { query ->
            articleDao.selectWorkbookMappedArticleRecords(query)
        }
    }
}
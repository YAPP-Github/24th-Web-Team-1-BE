package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.ReadArticleIdByWorkbookIdAndDayDto
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.query.SelectArticleIdByWorkbookIdAndDayQuery
import org.springframework.stereotype.Service

@Service
class SubscriptionArticleService(
    private val articleDao: ArticleDao,
) {
    fun readArticleIdByWorkbookIdAndDay(dto: ReadArticleIdByWorkbookIdAndDayDto): Long? {
        SelectArticleIdByWorkbookIdAndDayQuery(dto.workbookId, dto.day).let { query ->
            return articleDao.selectArticleIdByWorkbookIdAndDay(query)
        }
    }
}
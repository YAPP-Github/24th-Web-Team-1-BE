package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.ReadArticleContentInDto
import com.few.api.domain.subscription.service.dto.ReadArticleContentOutDto
import com.few.api.domain.subscription.service.dto.ReadArticleIdByWorkbookIdAndDayDto
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.query.SelectArticleContentQuery
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

    fun readArticleContent(dto: ReadArticleContentInDto): ReadArticleContentOutDto? {
        return SelectArticleContentQuery(dto.articleId).let { query ->
            articleDao.selectArticleContent(query)
        }?.let {
            ReadArticleContentOutDto(
                it.id,
                it.category,
                it.articleTitle,
                it.articleContent,
                it.writerName,
                it.writerLink
            )
        }
    }
}
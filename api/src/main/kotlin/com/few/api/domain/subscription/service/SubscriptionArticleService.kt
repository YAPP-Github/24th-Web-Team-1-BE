package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.ReadArticleContentInDto
import com.few.api.domain.subscription.service.dto.ReadArticleContentOutDto
import com.few.api.domain.subscription.service.dto.ReadArticleIdByWorkbookIdAndDayDto
import com.few.api.domain.article.repo.ArticleDao
import com.few.api.domain.article.repo.query.SelectArticleContentQuery
import com.few.api.domain.article.repo.query.SelectArticleIdByWorkbookIdAndDayQuery
import org.springframework.stereotype.Service

@Service
class SubscriptionArticleService(
    private val articleDao: ArticleDao,
) {
    fun readArticleIdByWorkbookIdAndDay(dto: ReadArticleIdByWorkbookIdAndDayDto): Long? {
        return articleDao.selectArticleIdByWorkbookIdAndDay(
            SelectArticleIdByWorkbookIdAndDayQuery(
                dto.workbookId,
                dto.day
            )
        )
    }

    fun readArticleContent(dto: ReadArticleContentInDto): ReadArticleContentOutDto? {
        return articleDao.selectArticleContent(SelectArticleContentQuery(dto.articleId))?.let {
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
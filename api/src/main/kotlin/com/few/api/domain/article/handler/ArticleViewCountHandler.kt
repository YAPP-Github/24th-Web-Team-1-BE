package com.few.api.domain.article.handler

import com.few.api.repo.dao.article.ArticleViewCountDao
import com.few.api.repo.dao.article.command.ArticleViewCountCommand
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class ArticleViewCountHandler(
    private val articleViewCountDao: ArticleViewCountDao,
) {
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
    fun browseArticleViewCount(articleId: Long): Long {
        return (articleViewCountDao.selectArticleViewCount(ArticleViewCountCommand(articleId)) ?: 0L) + 1L
    }
}
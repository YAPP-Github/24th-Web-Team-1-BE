package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.AddArticleViewHisInDto
import com.few.api.domain.article.service.dto.ReadArticleViewsInDto
import com.few.api.repo.dao.article.ArticleViewHisDao
import com.few.api.repo.dao.article.command.ArticleViewHisCommand
import com.few.api.repo.dao.article.query.ArticleViewHisCountQuery
import org.springframework.stereotype.Service

@Service
class ArticleViewHisService(
    private val articleViewHisDao: ArticleViewHisDao,
) {
    fun addArticleViewHis(inDto: AddArticleViewHisInDto) {
        articleViewHisDao.insertArticleViewHis(
            ArticleViewHisCommand(inDto.articleId, inDto.memberId)
        )
    }

    fun readArticleViews(inDto: ReadArticleViewsInDto): Long {
        return articleViewHisDao.countArticleViews(
            ArticleViewHisCountQuery(inDto.articleId)
        )
    }
}
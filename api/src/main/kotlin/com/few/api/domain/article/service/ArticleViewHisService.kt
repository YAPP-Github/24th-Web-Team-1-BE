package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.ArticleViewHisInDto
import com.few.api.repo.dao.article.ArticleViewHisDao
import com.few.api.repo.dao.article.command.ArticleViewHisCommand
import org.springframework.stereotype.Service

@Service
class ArticleViewHisService(
    private val articleViewHisDao: ArticleViewHisDao,
) {
    fun addArticleViewHis(inDto: ArticleViewHisInDto) {
        articleViewHisDao.insertArticleViewHis(
            ArticleViewHisCommand(inDto.articleId, inDto.memberId)
        )
    }
}
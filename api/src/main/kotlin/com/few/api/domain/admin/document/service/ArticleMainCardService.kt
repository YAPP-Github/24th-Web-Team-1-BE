package com.few.api.domain.admin.document.service

import com.few.api.domain.admin.document.service.dto.AppendWorkbookToArticleMainCardInDto
import com.few.api.domain.admin.document.service.dto.InitializeArticleMainCardInDto
import com.few.api.repo.dao.article.ArticleMainCardDao
import com.few.api.repo.dao.article.command.ArticleMainCardExcludeWorkbookCommand
import com.few.api.repo.dao.article.command.UpdateArticleMainCardWorkbookCommand
import com.few.api.repo.dao.article.command.WorkbookCommand
import org.springframework.stereotype.Service

@Service
class ArticleMainCardService(
    val articleMainCardDao: ArticleMainCardDao,
) {
    fun initialize(inDto: InitializeArticleMainCardInDto) {
        articleMainCardDao.insertArticleMainCard(
            ArticleMainCardExcludeWorkbookCommand(
                articleId = inDto.articleId,
                articleTitle = inDto.articleTitle,
                mainImageUrl = inDto.mainImageUrl,
                categoryCd = inDto.categoryCd,
                createdAt = inDto.createdAt,
                writerId = inDto.writerId,
                writerEmail = inDto.writerEmail,
                writerName = inDto.writerName,
                writerImgUrl = inDto.writerImgUrl
            )
        )
    }

    fun appendWorkbook(inDto: AppendWorkbookToArticleMainCardInDto) {
        articleMainCardDao.updateArticleMainCardSetWorkbook(
            UpdateArticleMainCardWorkbookCommand(
                articleId = inDto.articleId,
                workbooks = inDto.workbooks.map { WorkbookCommand(it.id, it.title) }
            )
        )
    }
}
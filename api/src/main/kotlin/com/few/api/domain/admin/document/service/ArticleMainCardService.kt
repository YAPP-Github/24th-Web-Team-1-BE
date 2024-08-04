package com.few.api.domain.admin.document.service

import com.few.api.domain.admin.document.service.dto.AppendWorkbookToArticleMainCardInDto
import com.few.api.domain.admin.document.service.dto.InitializeArticleMainCardInDto
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.article.ArticleMainCardDao
import com.few.api.repo.dao.article.command.ArticleMainCardExcludeWorkbookCommand
import com.few.api.repo.dao.article.command.UpdateArticleMainCardWorkbookCommand
import com.few.api.repo.dao.article.command.WorkbookCommand
import com.few.api.repo.dao.article.record.ArticleMainCardRecord
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import org.springframework.stereotype.Service

@Service
class ArticleMainCardService(
    val articleMainCardDao: ArticleMainCardDao,
    val workbookDao: WorkbookDao,
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
        val workbookRecord = workbookDao.selectWorkBook(SelectWorkBookRecordQuery(inDto.workbookId))
            ?: throw NotFoundException("workbook.notfound.id")

        val toBeAddedWorkbook = WorkbookCommand(inDto.workbookId, workbookRecord.title)

        val articleMainCardRecord: ArticleMainCardRecord =
            articleMainCardDao.selectArticleMainCardsRecord(setOf(inDto.articleId))
                .ifEmpty { throw NotFoundException("articlemaincard.notfound.id") }
                .first()

        val workbookCommands =
            articleMainCardRecord.workbooks.map { WorkbookCommand(it.id!!, it.title!!) }.toMutableList()
        workbookCommands.add(toBeAddedWorkbook)

        articleMainCardDao.updateArticleMainCardSetWorkbook(
            UpdateArticleMainCardWorkbookCommand(
                articleId = inDto.articleId,
                workbooks = workbookCommands
            )
        )
    }
}
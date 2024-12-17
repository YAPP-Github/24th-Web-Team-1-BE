package com.few.api.domain.admin.service

import com.few.api.domain.admin.service.dto.AppendWorkbookToArticleMainCardInDto
import com.few.api.domain.admin.service.dto.InitializeArticleMainCardInDto
import com.few.api.domain.article.repo.ArticleMainCardDao
import com.few.api.domain.article.repo.command.ArticleMainCardExcludeWorkbookCommand
import com.few.api.domain.article.repo.command.UpdateArticleMainCardWorkbookCommand
import com.few.api.domain.article.repo.command.WorkbookCommand
import com.few.api.domain.article.repo.record.ArticleMainCardRecord
import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.workbook.repo.WorkbookDao
import com.few.api.domain.workbook.repo.query.SelectWorkBookRecordQuery
import org.springframework.stereotype.Service

@Service
class AdminArticleMainCardService(
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
                writerUrl = inDto.writerUrl,
                writerImgUrl = inDto.writerImgUrl,
            ),
        )
    }

    fun appendWorkbook(inDto: AppendWorkbookToArticleMainCardInDto) {
        val workbookRecord =
            workbookDao.selectWorkBook(SelectWorkBookRecordQuery(inDto.workbookId))
                ?: throw NotFoundException("workbook.notfound.id")

        val toBeAddedWorkbook = WorkbookCommand(inDto.workbookId, workbookRecord.title)

        val articleMainCardRecord: ArticleMainCardRecord =
            articleMainCardDao
                .selectArticleMainCardsRecord(setOf(inDto.articleId))
                .firstOrNull() ?: throw NotFoundException("article.notfound.id")

        val workbookCommands =
            articleMainCardRecord.workbooks
                .map { WorkbookCommand(it.id!!, it.title!!) }
                .toMutableList()
                .apply { add(toBeAddedWorkbook) }

        articleMainCardDao.updateArticleMainCardSetWorkbook(
            UpdateArticleMainCardWorkbookCommand(
                articleId = inDto.articleId,
                workbooks = workbookCommands,
            ),
        )
    }
}
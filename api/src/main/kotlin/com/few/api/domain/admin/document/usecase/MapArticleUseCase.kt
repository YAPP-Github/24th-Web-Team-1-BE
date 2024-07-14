package com.few.api.domain.admin.document.usecase

import com.few.api.domain.admin.document.usecase.dto.MapArticleUseCaseIn
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.command.MapWorkBookToArticleCommand
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MapArticleUseCase(
    private val workbookDao: WorkbookDao,
) {
    @Transactional
    fun execute(useCaseIn: MapArticleUseCaseIn) {
        MapWorkBookToArticleCommand(useCaseIn.workbookId, useCaseIn.articleId, useCaseIn.dayCol).let { command ->
            workbookDao.mapWorkBookToArticle(command)
        }
    }
}
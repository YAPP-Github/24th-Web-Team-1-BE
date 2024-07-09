package com.few.api.domain.admin.document.usecase

import com.few.api.domain.admin.document.usecase.dto.AddWorkbookUseCaseIn
import com.few.api.domain.admin.document.usecase.dto.AddWorkbookUseCaseOut
import com.few.api.exception.common.InsertException
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.command.InsertWorkBookCommand
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AddWorkbookUseCase(
    private val workbookDao: WorkbookDao
) {
    @Transactional
    fun execute(useCaseIn: AddWorkbookUseCaseIn): AddWorkbookUseCaseOut {
        val workbookId = InsertWorkBookCommand(
            title = useCaseIn.title,
            mainImageUrl = useCaseIn.mainImageUrl,
            category = useCaseIn.category,
            description = useCaseIn.description
        ).let {
            workbookDao.insertWorkBook(it)
        } ?: throw InsertException("workbook.insertfail.record")

        return AddWorkbookUseCaseOut(workbookId)
    }
}
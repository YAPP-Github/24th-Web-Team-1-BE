package com.few.api.domain.workbook.usecase

import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseIn
import com.few.api.domain.workbook.usecase.dto.ReadWorkbookUseCaseOut
import com.few.api.domain.workbook.service.*
import com.few.api.domain.workbook.service.dto.BrowseWorkbookArticlesInDto
import com.few.api.domain.workbook.service.dto.BrowseWriterRecordsInDto
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import com.few.data.common.code.CategoryType
import org.springframework.stereotype.Component

@Component
class ReadWorkbookUseCase(
    private val workbookDao: WorkbookDao,
    private val workbookArticleService: WorkbookArticleService,
    private val workbookMemberService: WorkbookMemberService,
) {

    fun execute(useCaseIn: ReadWorkbookUseCaseIn): ReadWorkbookUseCaseOut {
        val workbookId = useCaseIn.workbookId

        val workbookRecord = workbookDao.selectWorkBook(SelectWorkBookRecordQuery(workbookId))
            ?: throw NotFoundException("workbook.notfound.id")

        val workbookMappedArticles =
            workbookArticleService.browseWorkbookArticles(BrowseWorkbookArticlesInDto(workbookId))

        val writerRecords = workbookMemberService.browseWriterRecords(
            BrowseWriterRecordsInDto(
                workbookMappedArticles.writerIds()
            )
        )

        return ReadWorkbookUseCaseOut(
            id = workbookRecord.id,
            mainImageUrl = workbookRecord.mainImageUrl,
            title = workbookRecord.title,
            description = workbookRecord.description,
            category = CategoryType.convertToDisplayName(workbookRecord.category),
            createdAt = workbookRecord.createdAt,
            writers = writerRecords.toWriterDetails(),
            articles = workbookMappedArticles.toArticleDetails()
        )
    }
}
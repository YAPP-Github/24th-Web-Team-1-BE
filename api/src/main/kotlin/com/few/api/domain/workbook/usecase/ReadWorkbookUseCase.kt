package com.few.api.domain.workbook.usecase

import com.few.api.domain.workbook.dto.ReadWorkbookUseCaseIn
import com.few.api.domain.workbook.dto.ReadWorkbookUseCaseOut
import com.few.api.domain.workbook.service.*
import com.few.api.domain.workbook.service.dto.BrowseWorkbookArticlesQuery
import com.few.api.domain.workbook.service.dto.BrowseWriterRecordsQuery
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import org.springframework.stereotype.Component

@Component
class ReadWorkbookUseCase(
    private val workbookDao: WorkbookDao,
    private val workbookArticleService: WorkbookArticleService,
    private val workbookMemberService: WorkbookMemberService
) {

    fun execute(useCaseIn: ReadWorkbookUseCaseIn): ReadWorkbookUseCaseOut {
        val workbookId = useCaseIn.workbookId

        val workbookRecord = SelectWorkBookRecordQuery(workbookId).let { query ->
            workbookDao.selectWorkBook(query) ?: throw RuntimeException("WorkBook with ID ${query.id} not found")
        }

        val workbookMappedArticles = BrowseWorkbookArticlesQuery(workbookId).let { query ->
            workbookArticleService.browseWorkbookArticles(query)
        }

        val writerRecords = BrowseWriterRecordsQuery(workbookMappedArticles.writerIds()).let { query ->
            workbookMemberService.browseWriterRecords(query)
        }

        return ReadWorkbookUseCaseOut(
            id = workbookRecord.id,
            mainImageUrl = workbookRecord.mainImageUrl,
            title = workbookRecord.title,
            description = workbookRecord.description,
            category = workbookRecord.category.toString(), // todo fix enum to string
            createdAt = workbookRecord.createdAt,
            writers = writerRecords.toWriterDetails(),
            articles = workbookMappedArticles.toArticleDetails()
        )
    }
}
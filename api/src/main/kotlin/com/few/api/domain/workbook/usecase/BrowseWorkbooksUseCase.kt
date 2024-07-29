package com.few.api.domain.workbook.usecase

import com.few.api.domain.workbook.service.WorkbookMemberService
import com.few.api.domain.workbook.service.dto.BrowseWorkbookWriterRecordsInDto
import com.few.api.domain.workbook.usecase.dto.BrowseWorkBookDetail
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseIn
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseOut
import com.few.api.domain.workbook.usecase.dto.WriterDetail
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.query.BrowseWorkBookQueryWithSubscriptionCount
import com.few.data.common.code.CategoryType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BrowseWorkbooksUseCase(
    private val workbookDao: WorkbookDao,
    private val workbookMemberService: WorkbookMemberService,
) {

    @Transactional
    fun execute(useCaseIn: BrowseWorkbooksUseCaseIn): BrowseWorkbooksUseCaseOut {
        val workbookRecords = BrowseWorkBookQueryWithSubscriptionCount(useCaseIn.category.code).let { query ->
            workbookDao.browseWorkBookWithSubscriptionCount(query)
        }

        val workbookIds = workbookRecords.map { it.id }
        val writerRecords = BrowseWorkbookWriterRecordsInDto(workbookIds).let { query ->
            workbookMemberService.browseWorkbookWriterRecords(query)
        }

        val workbookDetails = workbookRecords.map { record ->
            BrowseWorkBookDetail(
                id = record.id,
                mainImageUrl = record.mainImageUrl,
                title = record.title,
                description = record.description,
                category = CategoryType.convertToDisplayName(record.category),
                createdAt = record.createdAt,
                writerDetails = writerRecords[record.id]?.map {
                    WriterDetail(
                        id = it.writerId,
                        name = it.name,
                        url = it.url
                    )
                } ?: emptyList(),
                subscriptionCount = record.subscriptionCount
            )
        }

        return BrowseWorkbooksUseCaseOut(
            workbooks = workbookDetails
        )
    }
}
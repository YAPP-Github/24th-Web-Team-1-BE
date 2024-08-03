package com.few.api.domain.workbook.usecase

import com.few.api.domain.workbook.service.WorkbookMemberService
import com.few.api.domain.workbook.service.WorkbookSubscribeService
import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksInDto
import com.few.api.domain.workbook.service.dto.BrowseWorkbookWriterRecordsInDto
import com.few.api.domain.workbook.usecase.dto.BrowseWorkBookDetail
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseIn
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseOut
import com.few.api.domain.workbook.usecase.dto.WriterDetail
import com.few.api.domain.workbook.usecase.model.BasicWorkbookOrderDelegator
import com.few.api.domain.workbook.usecase.model.MainViewWorkbookOrderDelegator
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.query.BrowseWorkBookQueryWithSubscriptionCount
import com.few.api.web.support.ViewCategory
import com.few.data.common.code.CategoryType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

enum class WorkBookOrderStrategy {
    BASIC,

    /**
     * 메인 화면에 보여질 워크북을 정렬합니다.
     * - view의 값이 MAIN_CARD이다.
     * - memberId가 null이 아니다.
     * */
    MAIN_VIEW,
}

@Component
class BrowseWorkbooksUseCase(
    private val workbookDao: WorkbookDao,
    private val workbookMemberService: WorkbookMemberService,
    private val workbookSubscribeService: WorkbookSubscribeService,
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

        val orderStrategy = when {
            useCaseIn.viewCategory == ViewCategory.MAIN_CARD && useCaseIn.memberId != null -> WorkBookOrderStrategy.MAIN_VIEW
            else -> WorkBookOrderStrategy.BASIC
        }

        val orderedWorkbooks = when (orderStrategy) {
            WorkBookOrderStrategy.MAIN_VIEW -> {
                BrowseMemberSubscribeWorkbooksInDto(useCaseIn.memberId!!).let { dto ->
                    workbookSubscribeService.browseMemberSubscribeWorkbooks(dto)
                }.let { memberSubscribeWorkbooks ->
                    MainViewWorkbookOrderDelegator(workbookDetails, memberSubscribeWorkbooks)
                }
            }
            else -> BasicWorkbookOrderDelegator(workbookDetails)
        }.order()

        return BrowseWorkbooksUseCaseOut(
            workbooks = orderedWorkbooks
        )
    }
}
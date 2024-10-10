package com.few.api.domain.workbook.usecase

import com.few.api.domain.workbook.service.WorkbookMemberService
import com.few.api.domain.workbook.service.WorkbookSubscribeService
import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksInDto
import com.few.api.domain.workbook.service.dto.BrowseWorkbookWriterRecordsInDto
import com.few.api.domain.workbook.service.dto.WriterMappedWorkbookOutDto
import com.few.api.domain.workbook.usecase.dto.BrowseWorkBookDetail
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseIn
import com.few.api.domain.workbook.usecase.dto.BrowseWorkbooksUseCaseOut
import com.few.api.domain.workbook.usecase.dto.WriterDetail
import com.few.api.domain.workbook.usecase.model.*
import com.few.api.domain.workbook.usecase.model.order.*
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.query.BrowseWorkBookQueryWithSubscriptionCountQuery
import com.few.api.repo.dao.workbook.record.SelectWorkBookRecordWithSubscriptionCount
import com.few.api.web.support.ViewCategory
import com.few.data.common.code.CategoryType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

enum class WorkBookOrderStrategy {
    BASIC,

    /**
     * 로그인 상태에서 메인 화면에 보여질 워크북을 정렬합니다.
     * - view의 값이 MAIN_CARD이다.
     * - memberId가 null이 아니다.
     * */
    MAIN_VIEW_AUTH,

    /**
     * 비로그인 상태에서 메인 화면에 보여질 워크북을 정렬합니다.
     * - view의 값이 MAIN_CARD이다.
     * - memberId가 null이다.
     */
    MAIN_VIEW_UNAUTH,
}

@Component
class BrowseWorkbooksUseCase(
    private val workbookDao: WorkbookDao,
    private val workbookMemberService: WorkbookMemberService,
    private val workbookSubscribeService: WorkbookSubscribeService,
) {

    @Transactional
    fun execute(useCaseIn: BrowseWorkbooksUseCaseIn): BrowseWorkbooksUseCaseOut {
        val workbookRecords = workbookDao.browseWorkBookWithSubscriptionCount(
            BrowseWorkBookQueryWithSubscriptionCountQuery(useCaseIn.category.code)
        )

        val workbookIds = workbookRecords.map { it.id }
        val writerRecords = workbookMemberService.browseWorkbookWriterRecords(
            BrowseWorkbookWriterRecordsInDto(workbookIds)
        )

        val orderStrategy = getOrderStrategy(useCaseIn)
        val orderDelegator = when (orderStrategy) {
            WorkBookOrderStrategy.MAIN_VIEW_AUTH -> {
                genAuthMainViewWorkbookOrderDelegator(useCaseIn)
            }
            /** BASIC, MAIN_VIEW_UNAUTH -> 해당 경우는 DB 조회 결과를 그대로 반환 */
            else -> null
        }

        val workbooks = toWorkbooks(workbookRecords, writerRecords)

        val orderedWorkbook = OrderTargetWorkBooks(workbooks).let { target ->
            orderDelegator
                ?.let { delegator ->
                    UnOrderedWorkBooks(
                        target,
                        delegator
                    ).order()
                }
                ?: run { OrderedWorkBooks(target) }
        }.orderedWorkbooks

        val orderedWorkbookData = orderedWorkbook.workbookData
        orderedWorkbookData.map { workBook ->
            BrowseWorkBookDetail(
                id = workBook.id,
                mainImageUrl = workBook.mainImageUrl,
                title = workBook.title,
                description = workBook.description,
                category = workBook.category,
                createdAt = workBook.createdAt,
                writerDetails = workBook.writerDetails.map {
                    WriterDetail(
                        id = it.id,
                        name = it.name,
                        url = it.url
                    )
                },
                subscriptionCount = workBook.subscriptionCount
            )
        }.let {
            return BrowseWorkbooksUseCaseOut(
                workbooks = it
            )
        }
    }

    private fun getOrderStrategy(useCaseIn: BrowseWorkbooksUseCaseIn) =
        when {
            useCaseIn.viewCategory == ViewCategory.MAIN_CARD && useCaseIn.memberId != null -> WorkBookOrderStrategy.MAIN_VIEW_AUTH
            useCaseIn.viewCategory == ViewCategory.MAIN_CARD && useCaseIn.memberId == null -> WorkBookOrderStrategy.MAIN_VIEW_UNAUTH
            else -> WorkBookOrderStrategy.BASIC
        }

    private fun genAuthMainViewWorkbookOrderDelegator(useCaseIn: BrowseWorkbooksUseCaseIn): WorkbookOrderDelegator {
        return BrowseMemberSubscribeWorkbooksInDto(useCaseIn.memberId!!).let { dto ->
            workbookSubscribeService.browseMemberSubscribeWorkbooks(dto)
        }.map {
            MemberSubscribedWorkbook(
                workbookId = it.workbookId,
                isActiveSub = it.isActiveSub,
                currentDay = it.currentDay
            )
        }.let { subscribedWorkbooks ->
            AuthMainViewWorkbookOrderDelegator(subscribedWorkbooks)
        }
    }

    private fun toWorkbooks(
        workbookRecords: List<SelectWorkBookRecordWithSubscriptionCount>,
        writerRecords: Map<Long, List<WriterMappedWorkbookOutDto>>,
    ): WorkBooks {
        return workbookRecords.map { record ->
            WorkBook(
                id = record.id,
                mainImageUrl = record.mainImageUrl,
                title = record.title,
                description = record.description,
                category = CategoryType.convertToDisplayName(record.category),
                createdAt = record.createdAt,
                writerDetails = writerRecords[record.id]?.map {
                    WorkBookWriter(
                        id = it.writerId,
                        name = it.name,
                        url = it.url
                    )
                } ?: emptyList(),
                subscriptionCount = record.subscriptionCount
            )
        }.let {
            WorkBooks(it)
        }
    }
}
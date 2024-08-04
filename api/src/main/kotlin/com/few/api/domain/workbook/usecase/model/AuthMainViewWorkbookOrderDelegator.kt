package com.few.api.domain.workbook.usecase.model

import com.few.api.domain.workbook.service.dto.BrowseMemberSubscribeWorkbooksOutDto
import com.few.api.domain.workbook.usecase.dto.BrowseWorkBookDetail

class AuthMainViewWorkbookOrderDelegator(
    /**
     * @see com.few.api.repo.dao.workbook.WorkbookDao.browseWorkBookWithSubscriptionCount
     */
    private val workbooks: List<BrowseWorkBookDetail>,
    private val memberSubscribeWorkbooks: List<BrowseMemberSubscribeWorkbooksOutDto>,
) : WorkbookOrderDelegator {

    /**
     * 메인 화면에 보여질 워크북을 정렬합니다.
     * 1. 활성화된 구독 워크북을 먼저 보여줍니다.
     * 2. 구독 기록이 없는 워크북을 보여줍니다.
     * 3. 비활성화된 구독 워크북을 보여줍니다.
     */
    override fun order(): List<BrowseWorkBookDetail> {
        val allWorkbookIds = workbooks.associate { it.id to false }.toMutableMap()
        val activeSubWorkbookIds = memberSubscribeWorkbooks.filter { it.isActiveSub }.sortedByDescending {
            it.currentDay
        }.map { it.workbookId }
        val inActiveSubWorkbookIds = memberSubscribeWorkbooks.filter { !it.isActiveSub }.map { it.workbookId }

        val orderedWorkbooks = mutableListOf<BrowseWorkBookDetail>()

        /**
         * 활성화된 구독 워크북을 먼저 보여줍니다.
         */
        activeSubWorkbookIds.forEach { activeSubWorkbookId ->
            workbooks.find { it.id == activeSubWorkbookId }?.let {
                orderedWorkbooks.add(it)
                allWorkbookIds[activeSubWorkbookId] = true
            }
        }

        /**
         * 비활성화된 구독 워크북을 모아둡니다.
         */
        val lastAddWorkbooks = mutableListOf<BrowseWorkBookDetail>()
        inActiveSubWorkbookIds.forEach { inActiveSubWorkbookId ->
            workbooks.find { it.id == inActiveSubWorkbookId }?.let {
                lastAddWorkbooks.add(it)
                allWorkbookIds[inActiveSubWorkbookId] = true
            }
        }

        /**
         * 구독 기록이 없는 워크북을 보여줍니다.
         */
        allWorkbookIds.filter { !it.value }.forEach { (id, _) ->
            workbooks.find { it.id == id }?.let {
                orderedWorkbooks.add(it)
            }
        }

        /**
         * 비활성화된 구독 워크북을 보여줍니다.
         */
        orderedWorkbooks.addAll(lastAddWorkbooks)

        return orderedWorkbooks
    }
}
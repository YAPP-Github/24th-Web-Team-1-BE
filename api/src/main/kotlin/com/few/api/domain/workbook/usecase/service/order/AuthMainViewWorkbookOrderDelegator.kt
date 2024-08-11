package com.few.api.domain.workbook.usecase.service.order

import com.few.api.domain.workbook.usecase.model.MemberSubscribedWorkbook
import com.few.api.domain.workbook.usecase.model.WorkBook

class AuthMainViewWorkbookOrderDelegator(
    /**
     * @see com.few.api.repo.dao.workbook.WorkbookDao.browseWorkBookWithSubscriptionCount
     */
    private val workbooks: List<WorkBook>,
    private val memberSubscribedWorkbooks: List<MemberSubscribedWorkbook>,
) : WorkbookOrderDelegator {

    /**
     * 메인 화면에 보여질 워크북을 정렬합니다.
     * 1. 활성화된 구독 워크북을 먼저 보여줍니다.
     *    - 구독 워크북 정렬 기준은 currentDay를 기준으로 내림차순입니다.
     * 2. 구독 기록이 없는 워크북을 보여줍니다.
     * 3. 비활성화된 구독 워크북을 보여줍니다.
     */
    override fun order(): List<WorkBook> {
        val allWorkbookIds = workbooks.associate { it.id to false }.toMutableMap()
        val activeSubWorkbookIds =
            memberSubscribedWorkbooks.filter { it.isActiveSub }.sortedByDescending {
                it.currentDay
            }.map { it.workbookId }
        val inActiveSubWorkbookIds =
            memberSubscribedWorkbooks.filter { !it.isActiveSub }.map { it.workbookId }

        val orderedWorkbooks = mutableListOf<WorkBook>()

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
        val lastAddWorkbooks = mutableListOf<WorkBook>()
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
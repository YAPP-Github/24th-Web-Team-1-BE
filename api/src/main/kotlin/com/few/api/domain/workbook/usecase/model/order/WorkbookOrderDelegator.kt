package com.few.api.domain.workbook.usecase.model.order

interface WorkbookOrderDelegator {

    /**
     * 워크북을 정렬합니다.
     * @param targetWorkBooks 정렬할 워크북 목록
     * @return 정렬된 워크북 목록
     */
    fun order(targetWorkBooks: OrderTargetWorkBooks): OrderTargetWorkBooks
}
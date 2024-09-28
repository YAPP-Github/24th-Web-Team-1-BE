package com.few.api.domain.workbook.usecase.model.order

import com.few.api.domain.workbook.usecase.model.OrderedWorkBooks
import com.few.api.domain.workbook.usecase.model.WorkBook

interface WorkbookOrderDelegator {

    /**
     * 워크북을 정렬합니다.
     * @param workbooks 정렬할 워크북 목록
     * @return 정렬된 워크북 목록
     */
    fun order(workbooks: List<WorkBook>): OrderedWorkBooks
}
package com.few.api.domain.workbook.usecase.service.order

import com.few.api.domain.workbook.usecase.model.WorkBook

interface WorkbookOrderDelegator {

    /**
     * 워크북을 정렬합니다.
     * */
    fun order(): List<WorkBook>
}
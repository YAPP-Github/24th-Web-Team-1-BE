package com.few.api.domain.workbook.usecase.model

import com.few.api.domain.workbook.usecase.model.order.WorkbookOrderDelegator

open class WorkBooks(
    private val workbooks: List<WorkBook>,
) {
    fun order(delegator: WorkbookOrderDelegator): OrderedWorkBooks {
        return delegator.order(workbooks)
    }
}
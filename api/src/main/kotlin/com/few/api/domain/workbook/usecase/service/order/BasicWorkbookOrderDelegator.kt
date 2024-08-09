package com.few.api.domain.workbook.usecase.service.order

import com.few.api.domain.workbook.usecase.model.WorkBook

class BasicWorkbookOrderDelegator(
    private val workbooks: List<WorkBook>,
) : WorkbookOrderDelegator {
    override fun order(): List<WorkBook> {
        return workbooks
    }
}
package com.few.api.domain.workbook.usecase.model.order

import com.few.api.domain.workbook.usecase.model.OrderedWorkBooks
import com.few.api.domain.workbook.usecase.model.WorkBook

class BasicWorkbookOrderDelegator : WorkbookOrderDelegator {
    override fun order(workbooks: List<WorkBook>): OrderedWorkBooks {
        return OrderedWorkBooks(workbooks)
    }
}
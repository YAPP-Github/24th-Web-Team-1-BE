package com.few.api.domain.workbook.usecase.service.order

import com.few.api.domain.workbook.usecase.model.WorkBook
import org.springframework.stereotype.Service

@Service
class WorkbookOrderDelegatorExecutor {

    fun execute(delegator: WorkbookOrderDelegator): List<WorkBook> {
        return delegator.order()
    }
}
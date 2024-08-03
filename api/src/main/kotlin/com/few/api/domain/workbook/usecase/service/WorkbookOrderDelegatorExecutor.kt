package com.few.api.domain.workbook.usecase.service

import com.few.api.domain.workbook.usecase.dto.BrowseWorkBookDetail
import com.few.api.domain.workbook.usecase.model.WorkbookOrderDelegator
import org.springframework.stereotype.Service

@Service
class WorkbookOrderDelegatorExecutor {

    fun execute(delegator: WorkbookOrderDelegator): List<BrowseWorkBookDetail> {
        return delegator.order()
    }
}
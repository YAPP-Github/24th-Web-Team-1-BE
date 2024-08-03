package com.few.api.domain.workbook.usecase.model

import com.few.api.domain.workbook.usecase.dto.BrowseWorkBookDetail

class BasicWorkbookOrderDelegator(
    private val workbooks: List<BrowseWorkBookDetail>,
) : WorkbookOrderDelegator {
    override fun order(): List<BrowseWorkBookDetail> {
        return workbooks
    }
}
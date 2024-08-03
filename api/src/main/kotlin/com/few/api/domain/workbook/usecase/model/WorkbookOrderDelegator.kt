package com.few.api.domain.workbook.usecase.model

import com.few.api.domain.workbook.usecase.dto.BrowseWorkBookDetail

interface WorkbookOrderDelegator {

    /**
     * 워크북을 정렬합니다.
     * */
    fun order(): List<BrowseWorkBookDetail>
}
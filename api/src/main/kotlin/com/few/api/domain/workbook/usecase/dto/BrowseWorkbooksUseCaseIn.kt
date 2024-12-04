package com.few.api.domain.workbook.usecase.dto

import com.few.api.domain.common.vo.ViewCategory
import com.few.api.domain.common.vo.WorkBookCategory

data class BrowseWorkbooksUseCaseIn(
    val category: WorkBookCategory,
    val viewCategory: ViewCategory?,
    val memberId: Long?,
)
package com.few.api.domain.workbook.usecase.dto

import com.few.api.web.support.ViewCategory
import com.few.api.web.support.WorkBookCategory

data class BrowseWorkbooksUseCaseIn(
    val category: WorkBookCategory,
    val viewCategory: ViewCategory?,
    val memberId: Long?,
)
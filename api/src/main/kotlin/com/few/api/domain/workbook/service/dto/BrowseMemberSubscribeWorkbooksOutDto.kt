package com.few.api.domain.workbook.service.dto

data class BrowseMemberSubscribeWorkbooksOutDto(
    val workbookId: Long,
    val isActiveSub: Boolean,
    val currentDay: Int,
)
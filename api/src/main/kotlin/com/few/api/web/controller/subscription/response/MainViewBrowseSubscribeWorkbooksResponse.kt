package com.few.api.web.controller.subscription.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.few.api.domain.workbook.usecase.dto.WriterDetail
import java.net.URL
import java.time.LocalDateTime

data class MainViewBrowseSubscribeWorkbooksResponse(
    val workbooks: List<MainViewSubscribeWorkbookInfo>,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MainViewSubscribeWorkbookInfo(
    val id: Long,
    val mainImageUrl: URL,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: LocalDateTime,
    val writerDetails: List<WriterDetail>,
    val subscriptionCount: Long,
    val status: String?, // convert from enum
    val totalDay: Int?,
    val currentDay: Int?,
    val rank: Long?,
    val totalSubscriber: Long?,
    val articleInfo: String?, // convert from Json
)
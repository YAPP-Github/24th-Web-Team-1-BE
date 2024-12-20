package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.*
import com.few.api.domain.workbook.repo.WorkbookDao
import com.few.api.domain.workbook.repo.query.SelectAllWorkbookTitleQuery
import com.few.api.domain.workbook.repo.query.SelectWorkBookLastArticleIdQuery
import com.few.api.domain.workbook.repo.query.SelectWorkBookRecordQuery
import org.springframework.stereotype.Service

@Service
class SubscriptionWorkbookService(
    private val workbookDao: WorkbookDao,
) {
    fun readWorkbookTitle(dto: ReadWorkbookTitleInDto): ReadWorkbookTitleOutDto? =
        workbookDao
            .selectWorkBook(SelectWorkBookRecordQuery(dto.workbookId))
            ?.title
            ?.let {
                ReadWorkbookTitleOutDto(
                    it,
                )
            }

    /**
     * key: workbookId
     * value: title
     */
    fun readAllWorkbookTitle(dto: ReadAllWorkbookTitleInDto): Map<Long, String> =
        workbookDao
            .selectAllWorkbookTitle(SelectAllWorkbookTitleQuery(dto.workbookIds))
            .associateBy({ it.workbookId }, { it.title })

    fun readWorkbookLastArticleId(dto: ReadWorkbookLastArticleIdInDto): ReadWorkbookLastArticleIdOutDto? =
        workbookDao
            .selectWorkBookLastArticleId(SelectWorkBookLastArticleIdQuery(dto.workbookId))
            ?.let {
                ReadWorkbookLastArticleIdOutDto(it)
            }
}
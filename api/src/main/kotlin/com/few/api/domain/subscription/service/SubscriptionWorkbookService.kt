package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.*
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.query.SelectAllWorkbookTitleQuery
import com.few.api.repo.dao.workbook.query.SelectWorkBookLastArticleIdQuery
import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import org.springframework.stereotype.Service

@Service
class SubscriptionWorkbookService(
    private val workbookDao: WorkbookDao,
) {

    fun readWorkbookTitle(dto: ReadWorkbookTitleInDto): ReadWorkbookTitleOutDto? {
        return workbookDao.selectWorkBook(SelectWorkBookRecordQuery(dto.workbookId))
            ?.title
            ?.let {
                ReadWorkbookTitleOutDto(
                    it
                )
            }
    }

    /**
     * key: workbookId
     * value: title
     */
    fun readAllWorkbookTitle(dto: ReadAllWorkbookTitleInDto): Map<Long, String> {
        return workbookDao.selectAllWorkbookTitle(SelectAllWorkbookTitleQuery(dto.workbookIds))
            .associateBy({ it.workbookId }, { it.title })
    }

    fun readWorkbookLastArticleId(dto: ReadWorkbookLastArticleIdInDto): ReadWorkbookLastArticleIdOutDto? {
        return workbookDao.selectWorkBookLastArticleId(SelectWorkBookLastArticleIdQuery(dto.workbookId))
            ?.let {
                ReadWorkbookLastArticleIdOutDto(it)
            }
    }
}
package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.ReadWorkbookLastArticleIdInDto
import com.few.api.domain.subscription.service.dto.ReadWorkbookLastArticleIdOutDto
import com.few.api.domain.subscription.service.dto.ReadWorkbookTitleInDto
import com.few.api.domain.subscription.service.dto.ReadWorkbookTitleOutDto
import com.few.api.repo.dao.workbook.WorkbookDao
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

    fun readWorkbookLastArticleId(dto: ReadWorkbookLastArticleIdInDto): ReadWorkbookLastArticleIdOutDto? {
        return workbookDao.selectWorkBookLastArticleId(SelectWorkBookLastArticleIdQuery(dto.workbookId))
            ?.let {
                ReadWorkbookLastArticleIdOutDto(it)
            }
    }
}
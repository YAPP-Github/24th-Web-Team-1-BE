package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.ReadWorkbookTitleInDto
import com.few.api.domain.subscription.service.dto.ReadWorkbookTitleOutDto
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import org.springframework.stereotype.Service

@Service
class WorkbookService(
    private val workbookDao: WorkbookDao
) {

    fun readWorkbookTitle(dto: ReadWorkbookTitleInDto): ReadWorkbookTitleOutDto? {
        return SelectWorkBookRecordQuery(dto.workbookId).let { query ->
            workbookDao.selectWorkBook(query)?.title?.let { ReadWorkbookTitleOutDto(it) }
        }
    }
}
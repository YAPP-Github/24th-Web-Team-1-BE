package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.ReadWorkbookTitleDto
import com.few.api.repo.dao.workbook.WorkbookDao
import com.few.api.repo.dao.workbook.query.SelectWorkBookRecordQuery
import org.springframework.stereotype.Service

@Service
class WorkbookService(
    private val workbookDao: WorkbookDao
) {

    fun readWorkbookTitle(dto: ReadWorkbookTitleDto): String? {
        return SelectWorkBookRecordQuery(dto.workbookId).let { query ->
            workbookDao.selectWorkBook(query)?.title
        }
    }
}
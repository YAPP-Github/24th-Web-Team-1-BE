package com.few.api.domain.workbook.service

import com.few.api.domain.workbook.service.dto.BrowseWorkbookWriterRecordsInDto
import com.few.api.domain.workbook.usecase.dto.WriterDetail
import com.few.api.domain.workbook.service.dto.BrowseWriterRecordsInDto
import com.few.api.domain.workbook.service.dto.WriterMappedWorkbookOutDto
import com.few.api.domain.workbook.service.dto.WriterOutDto
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.BrowseWorkbookWritersQuery
import com.few.api.repo.dao.member.query.SelectWritersQuery
import org.springframework.stereotype.Service

fun List<WriterOutDto>.toWriterDetails(): List<WriterDetail> {
    return this.map { WriterDetail(it.writerId, it.name, it.url) }
}

@Service
class WorkbookMemberService(
    private val memberDao: MemberDao,
) {
    fun browseWriterRecords(query: BrowseWriterRecordsInDto): List<WriterOutDto> {
        return memberDao.selectWriters(SelectWritersQuery(query.writerIds)).map { record ->
            WriterOutDto(
                writerId = record.writerId,
                name = record.name,
                url = record.url
            )
        }
    }

    /**
     * key: workbookId
     * value: writer list
     */
    fun browseWorkbookWriterRecords(query: BrowseWorkbookWriterRecordsInDto): Map<Long, List<WriterMappedWorkbookOutDto>> {
        return memberDao.selectWriters(BrowseWorkbookWritersQuery(query.workbookIds))
            .map { record ->
                WriterMappedWorkbookOutDto(
                    workbookId = record.workbookId,
                    writerId = record.writerId,
                    name = record.name,
                    url = record.url
                )
            }.groupBy { it.workbookId }
    }
}
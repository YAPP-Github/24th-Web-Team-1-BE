package com.few.api.domain.workbook.service

import com.few.api.domain.member.repo.MemberDao
import com.few.api.domain.member.repo.query.BrowseWorkbookWritersQuery
import com.few.api.domain.member.repo.query.SelectWritersQuery
import com.few.api.domain.workbook.service.dto.*
import org.springframework.stereotype.Service

fun List<WriterOutDto>.toWriterDetails(): List<WriterDetailDto> {
    return this.map { WriterDetailDto(it.writerId, it.name, it.url) }
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
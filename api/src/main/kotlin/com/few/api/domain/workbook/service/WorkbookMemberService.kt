package com.few.api.domain.workbook.service

import com.few.api.domain.workbook.dto.WriterDetail
import com.few.api.domain.workbook.service.dto.BrowseWriterRecordsQuery
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.SelectWritersQuery
import com.few.api.repo.dao.member.record.WriterRecord
import org.springframework.stereotype.Service

fun List<WriterRecord>.toWriterDetails(): List<WriterDetail> {
    return this.map { WriterDetail(it.writerId, it.name, it.url) }
}

@Service
class WorkbookMemberService(
    private val memberDao: MemberDao
) {
    fun browseWriterRecords(query: BrowseWriterRecordsQuery): List<WriterRecord> {
        return SelectWritersQuery(query.writerIds).let { query ->
            memberDao.selectWriters(query)
        }
    }
}
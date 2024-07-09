package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.ReadWriterRecordInDto
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.SelectWriterQuery
import com.few.api.repo.dao.member.record.WriterRecord
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReadArticleWriterRecordService(
    private val memberDao: MemberDao
) {

    @Transactional(readOnly = true)
    fun execute(query: ReadWriterRecordInDto): WriterRecord? {
        SelectWriterQuery(query.writerId).let { query: SelectWriterQuery ->
            return memberDao.selectWriter(query)
        }
    }
}
package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.ReadWriterOutDto
import com.few.api.domain.article.service.dto.ReadWriterRecordInDto
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.SelectWriterQuery
import org.springframework.stereotype.Service

@Service
class ReadArticleWriterRecordService(
    private val memberDao: MemberDao,
) {

    fun execute(query: ReadWriterRecordInDto): ReadWriterOutDto? {
        SelectWriterQuery(query.writerId).let { query: SelectWriterQuery ->
            val record = memberDao.selectWriter(query)
            return record?.let {
                ReadWriterOutDto(
                    writerId = it.writerId,
                    name = it.name,
                    url = it.url,
                    imageUrl = it.imageUrl
                )
            }
        }
    }
}
package com.few.api.domain.article.service

import com.few.api.domain.article.service.dto.ReadWriterOutDto
import com.few.api.domain.article.service.dto.ReadWriterRecordInDto
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.SelectWriterQuery
import org.springframework.stereotype.Service

@Suppress("NAME_SHADOWING")
@Service
class ReadArticleWriterRecordService(
    private val memberDao: MemberDao,
) {

    fun execute(query: ReadWriterRecordInDto): ReadWriterOutDto? {
        return memberDao.selectWriter(SelectWriterQuery(query.writerId))
            ?.let {
                ReadWriterOutDto(
                    writerId = it.writerId,
                    name = it.name,
                    url = it.url,
                    imageUrl = it.imageUrl
                )
            }
    }
}
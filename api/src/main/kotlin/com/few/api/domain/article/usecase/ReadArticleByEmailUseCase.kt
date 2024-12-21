package com.few.api.domain.article.usecase

import com.few.api.domain.article.service.ArticleLogService
import com.few.api.domain.article.service.ArticleMemberService
import com.few.api.domain.article.service.dto.InsertOpenEventDto
import com.few.api.domain.article.service.dto.ReadMemberByEmailDto
import com.few.api.domain.article.service.dto.SelectDeliveryEventByMessageIdDto
import com.few.api.domain.article.usecase.dto.ReadArticleByEmailUseCaseIn
import com.few.api.domain.common.exception.NotFoundException
import com.few.api.domain.common.vo.EmailLogEventType
import org.springframework.stereotype.Component
import repo.jooq.DataSourceTransactional

@Component
class ReadArticleByEmailUseCase(
    private val memberService: ArticleMemberService,
    private val articleLogService: ArticleLogService,
) {
    @DataSourceTransactional
    fun execute(useCaseIn: ReadArticleByEmailUseCaseIn) {
        val memberId =
            memberService.readMemberByEmail(ReadMemberByEmailDto(useCaseIn.destination[0]))
                ?: throw NotFoundException("member.notfound.email")

        val record =
            articleLogService.selectDeliveryEventByMessageId(
                SelectDeliveryEventByMessageIdDto(
                    useCaseIn.messageId,
                    EmailLogEventType.DELIVERY.code,
                ),
            )
                ?: throw IllegalStateException("event is not found")

        articleLogService.insertOpenEvent(
            InsertOpenEventDto(
                memberId = memberId,
                articleId = record.articleId,
                messageId = record.messageId,
                eventType = EmailLogEventType.OPEN.code,
                sendType = record.sendType,
            ),
        )
    }
}
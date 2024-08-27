package com.few.api.domain.subscription.handler

import com.few.api.config.DatabaseAccessThreadPoolConfig.Companion.DATABASE_ACCESS_POOL
import com.few.api.domain.subscription.service.SubscriptionArticleService
import com.few.api.domain.subscription.service.SubscriptionMemberService
import com.few.api.domain.subscription.service.SubscriptionEmailService
import com.few.api.domain.subscription.service.SubscriptionWorkbookService
import com.few.api.domain.subscription.service.dto.*
import com.few.api.exception.common.NotFoundException
import com.few.api.repo.dao.subscription.SubscriptionDao
import com.few.api.repo.dao.subscription.command.UpdateArticleProgressCommand
import com.few.api.repo.dao.subscription.command.UpdateLastArticleProgressCommand
import com.few.data.common.code.CategoryType
import com.few.email.service.article.dto.Content
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class SendWorkbookArticleAsyncHandler(
    private val memberService: SubscriptionMemberService,
    private val articleService: SubscriptionArticleService,
    private val workbookService: SubscriptionWorkbookService,
    private val subscriptionDao: SubscriptionDao,
    private val emailService: SubscriptionEmailService,
) {

    @Async(value = DATABASE_ACCESS_POOL)
    fun sendWorkbookArticle(memberId: Long, workbookId: Long, articleDayCol: Byte) {
        val date = LocalDate.now()
        val memberEmail = ReadMemberEmailInDto(memberId).let { memberService.readMemberEmail(it) }.let { it?.email }
            ?: throw NotFoundException("member.notfound.id")
        val article = ReadArticleIdByWorkbookIdAndDayDto(workbookId, articleDayCol.toInt()).let {
            articleService.readArticleIdByWorkbookIdAndDay(it)
        }?.let { articleId ->
            ReadArticleContentInDto(articleId).let {
                articleService.readArticleContent(it)
            }
        } ?: throw NotFoundException("article.notfound.id")

        SendArticleInDto(
            memberId = memberId,
            workbookId = workbookId,
            toEmail = memberEmail,
            articleDayCol = articleDayCol,
            articleTitle = article.articleTitle,
            articleContent = Content.create(
                memberEmail = memberEmail,
                workbookId = workbookId,
                articleId = article.id,
                currentDate = date,
                category = CategoryType.convertToDisplayName(article.category.toByte()),
                articleDay = articleDayCol.toInt(),
                articleTitle = article.articleTitle,
                writerName = article.writerName,
                writerLink = article.writerLink,
                articleContent = article.articleContent
            )
        ).let {
            runCatching { emailService.sendArticleEmail(it) }
                .onSuccess {
                    val lastDayArticleId = ReadWorkbookLastArticleIdInDto(
                        workbookId
                    ).let {
                        workbookService.readWorkbookLastArticleId(it)
                    }?.lastArticleId ?: throw NotFoundException("workbook.notfound.id")

                    if (article.id == lastDayArticleId) {
                        UpdateArticleProgressCommand(workbookId, memberId).let {
                            subscriptionDao.updateArticleProgress(it)
                        }
                    } else {
                        UpdateLastArticleProgressCommand(workbookId, memberId).let {
                            subscriptionDao.updateLastArticleProgress(it)
                        }
                    }
                }
        }
    }
}
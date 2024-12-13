package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.SendArticleInDto
import com.few.api.domain.article.email.SendArticleEmailService
import com.few.api.domain.article.email.dto.Content
import com.few.api.domain.article.email.dto.SendArticleEmailArgs
import org.springframework.stereotype.Service

@Service
class SubscriptionEmailService(
    private val sendArticleEmailService: SendArticleEmailService,
) {

    companion object {
        private const val ARTICLE_SUBJECT_TEMPLATE = "Day%d %s"
        private const val ARTICLE_TEMPLATE = "article"
    }

    fun sendArticleEmail(dto: SendArticleInDto): String {
        return sendArticleEmailService.send(
            SendArticleEmailArgs(
                dto.toEmail,
                ARTICLE_SUBJECT_TEMPLATE.format(
                    dto.articleDayCol,
                    dto.articleTitle
                ),
                ARTICLE_TEMPLATE,
                Content.create(
                    dto.articleContent.memberEmail,
                    dto.articleContent.workbookId,
                    dto.articleContent.articleId,
                    dto.articleContent.currentDate,
                    dto.articleContent.category,
                    dto.articleContent.articleDay,
                    dto.articleContent.articleTitle,
                    dto.articleContent.writerName,
                    dto.articleContent.writerLink,
                    dto.articleContent.articleContent
                )
            )
        )
    }
}
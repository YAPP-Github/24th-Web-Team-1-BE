package com.few.api.domain.subscription.service

import com.few.api.domain.subscription.service.dto.SendArticleInDto
import com.few.email.service.article.SendArticleEmailService
import com.few.email.service.article.dto.SendArticleEmailArgs
import org.springframework.stereotype.Service

@Service
class SubscriptionEmailService(
    private val sendArticleEmailService: SendArticleEmailService,
) {

    companion object {
        private const val ARTICLE_SUBJECT_TEMPLATE = "Day%d %s"
        private const val ARTICLE_TEMPLATE = "article"
    }

    fun sendArticleEmail(dto: SendArticleInDto) {
        SendArticleEmailArgs(
            dto.toEmail,
            ARTICLE_SUBJECT_TEMPLATE.format(
                dto.articleDayCol,
                dto.articleTitle
            ),
            ARTICLE_TEMPLATE,
            dto.articleContent
        ).let {
            sendArticleEmailService.send(it)
        }
    }
}
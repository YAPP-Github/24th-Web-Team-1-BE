package com.few.api.domain.article.email

import com.few.api.domain.article.email.dto.SendArticleEmailArgs
import email.EmailContext
import email.EmailSender
import email.EmailTemplateProcessor
import email.provider.ArticleAwsSESEmailSendProvider
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class SendArticleEmailService(
    mailProperties: MailProperties,
    emailSendProvider: ArticleAwsSESEmailSendProvider,
    private val emailTemplateProcessor: EmailTemplateProcessor,
) : EmailSender<SendArticleEmailArgs>(mailProperties, emailSendProvider) {
    override fun getHtml(args: SendArticleEmailArgs): String {
        val context = EmailContext()
        context.setVariable("articleLink", args.content.articleLink.toString() + "?fromEmail=true")
        context.setVariable(
            "currentDate",
            args.content.currentDate.format(
                DateTimeFormatter.ofPattern("yyyy/MM/dd EEEE").withLocale(
                    Locale.KOREA,
                ),
            ),
        )
        context.setVariable("category", args.content.category)
        context.setVariable("articleDay", "Day" + args.content.articleDay)
        context.setVariable("articleTitle", args.content.articleTitle)
        context.setVariable("writerName", args.content.writerName)
        context.setVariable("writerLink", args.content.writerLink)
        context.setVariable("articleContent", args.content.articleContent)
        context.setVariable("problemLink", args.content.problemLink)
        context.setVariable("unsubscribeLink", args.content.unsubscribeLink)
        return emailTemplateProcessor.process(args.template, context)
    }
}
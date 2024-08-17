package com.few.email.service.article

import com.few.email.sender.EmailSender
import com.few.email.sender.provider.EmailSendProvider
import com.few.email.service.article.dto.SendArticleEmailArgs
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class SendArticleEmailService(
    mailProperties: MailProperties,
    emailSendProvider: EmailSendProvider,
    private val templateEngine: TemplateEngine,
) : EmailSender<SendArticleEmailArgs>(mailProperties, emailSendProvider) {

    override fun getHtml(args: SendArticleEmailArgs): String {
        val context = Context()
        context.setVariable("articleLink", args.content.articleLink.toString() + "?fromEmail=true")
        context.setVariable(
            "currentDate",
            args.content.currentDate.format(
                DateTimeFormatter.ofPattern("yyyy/MM/dd EEEE").withLocale(
                    Locale.KOREA
                )
            )
        )
        context.setVariable("category", args.content.category)
        context.setVariable("articleDay", "Day" + args.content.articleDay)
        context.setVariable("articleTitle", args.content.articleTitle)
        context.setVariable("writerName", args.content.writerName)
        context.setVariable("writerLink", args.content.writerLink)
        context.setVariable("articleContent", args.content.articleContent)
        context.setVariable("problemLink", args.content.problemLink)
        context.setVariable("unsubscribeLink", args.content.unsubscribeLink)
        return templateEngine.process(args.template, context)
    }
}
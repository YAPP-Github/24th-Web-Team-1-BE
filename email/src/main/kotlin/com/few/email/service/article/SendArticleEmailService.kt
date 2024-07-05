package com.few.email.service.article

import com.few.email.sender.SendEmailSender
import com.few.email.service.article.dto.SendArticleEmailArgs
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class SendArticleEmailService(
    mailProperties: MailProperties,
    emailSender: JavaMailSender,
    private val templateEngine: TemplateEngine
) : SendEmailSender<SendArticleEmailArgs>(mailProperties, emailSender) {

    override fun getHtml(args: SendArticleEmailArgs): String {
        val context = Context()
        context.setVariable("articleLink", args.articleContent.articleLink.toString() + "?fromEmail=true")
        context.setVariable(
            "currentDate",
            args.articleContent.currentDate.format(
                DateTimeFormatter.ofPattern("yyyy/MM/dd EEEE").withLocale(
                    Locale.KOREA
                )
            )
        )
        context.setVariable("category", args.articleContent.category)
        context.setVariable("articleDay", "Day" + args.articleContent.articleDay)
        context.setVariable("articleTitle", args.articleContent.articleTitle)
        context.setVariable("writerName", args.articleContent.writerName)
        context.setVariable("writerLink", args.articleContent.writerLink)
        context.setVariable("articleContent", args.articleContent.articleContent)
        context.setVariable("problemLink", args.articleContent.problemLink)
        context.setVariable("unsubscribeLink", args.articleContent.unsubscribeLink)
        return templateEngine.process(args.template, context)
    }
}
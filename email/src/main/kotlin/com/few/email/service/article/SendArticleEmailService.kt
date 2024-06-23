package com.few.email.service.article

import com.few.email.sender.SendEmailSender
import com.few.email.service.article.dto.SendArticleEmailArgs
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class SendArticleEmailService(
    mailProperties: MailProperties,
    emailSender: JavaMailSender,
    private val templateEngine: TemplateEngine
) : SendEmailSender<SendArticleEmailArgs>(mailProperties, emailSender) {

    override fun getHtml(args: SendArticleEmailArgs): String {
        val context = Context()
        context.setVariable("content", args.articleContent)
        context.setVariable("style", args.style)
        return templateEngine.process(args.template, context)
    }
}
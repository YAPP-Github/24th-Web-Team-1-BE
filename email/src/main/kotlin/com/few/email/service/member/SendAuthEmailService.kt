package com.few.email.service.member

import com.few.email.sender.SendEmailSender
import com.few.email.service.member.dto.SendAuthEmailArgs
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*

@Component
class SendAuthEmailService(
    mailProperties: MailProperties,
    emailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
) : SendEmailSender<SendAuthEmailArgs>(mailProperties, emailSender) {

    override fun getHtml(args: SendAuthEmailArgs): String {
        val context = Context()
        context.setVariable("email", args.content.email)
        context.setVariable("confirmLink", args.content.confirmLink)
        return templateEngine.process(args.template, context)
    }
}
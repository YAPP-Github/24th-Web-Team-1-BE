package com.few.api.domain.member.email

import email.EmailSender
import email.provider.EmailSendProvider
import com.few.api.domain.member.email.dto.SendAuthEmailArgs
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class SendAuthEmailService(
    mailProperties: MailProperties,
    emailSendProvider: EmailSendProvider,
    private val templateEngine: TemplateEngine,
) : EmailSender<SendAuthEmailArgs>(mailProperties, emailSendProvider) {

    override fun getHtml(args: SendAuthEmailArgs): String {
        val context = Context()
        context.setVariable("email", args.content.email)
        context.setVariable("confirmLink", args.content.confirmLink)
        context.setVariable("headComment", args.content.headComment)
        context.setVariable("subComment", args.content.subComment)
        return templateEngine.process(args.template, context)
    }
}
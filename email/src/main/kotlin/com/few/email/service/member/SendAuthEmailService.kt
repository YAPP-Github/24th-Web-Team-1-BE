package com.few.email.service.member

import com.few.email.sender.EmailSender
import com.few.email.sender.provider.EmailSendProvider
import com.few.email.service.member.dto.SendAuthEmailArgs
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
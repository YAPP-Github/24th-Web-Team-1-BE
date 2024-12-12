package com.few.api.domain.member.email

import email.EmailSender
import email.provider.EmailSendProvider
import com.few.api.domain.member.email.dto.SendAuthEmailArgs
import email.EmailContext
import email.EmailTemplateProcessor
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.stereotype.Component

@Component
class SendAuthEmailService(
    mailProperties: MailProperties,
    emailSendProvider: EmailSendProvider,
    private val emailTemplateProcessor: EmailTemplateProcessor,
) : EmailSender<SendAuthEmailArgs>(mailProperties, emailSendProvider) {

    override fun getHtml(args: SendAuthEmailArgs): String {
        val context = EmailContext()
        context.setVariable("email", args.content.email)
        context.setVariable("confirmLink", args.content.confirmLink)
        context.setVariable("headComment", args.content.headComment)
        context.setVariable("subComment", args.content.subComment)
        return emailTemplateProcessor.process(args.template, context)
    }
}
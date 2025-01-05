package com.few.crm.email.service

import email.*
import email.provider.CrmAwsSESEmailSendProvider
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.stereotype.Component

data class SendEmailArgs(
    override val to: String,
    override val subject: String,
    override val template: String,
    override val content: NonContent,
    override val properties: String = "",
) : SendMailArgs<NonContent, String>

@Component
class CrmSendNonVariablesEmailService(
    mailProperties: MailProperties,
    emailSendProvider: CrmAwsSESEmailSendProvider,
    private val emailTemplateProcessor: EmailTemplateProcessor,
) : EmailSender<SendEmailArgs>(mailProperties, emailSendProvider) {
    override fun getHtml(args: SendEmailArgs): String {
        val context = EmailContext()
        return emailTemplateProcessor.process(args.template, context, EmailTemplateType.STRING)
    }
}

class NonContent
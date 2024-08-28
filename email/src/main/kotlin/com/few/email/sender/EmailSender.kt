package com.few.email.sender

import com.few.email.sender.dto.SendMailArgs
import com.few.email.sender.provider.EmailSendProvider
import org.springframework.boot.autoconfigure.mail.MailProperties

abstract class EmailSender<T : SendMailArgs<*, *>>(
    private val mailProperties: MailProperties,
    private val emailSendProvider: EmailSendProvider,
) {

    fun send(args: T) {
        val from = mailProperties.username
        val to = args.to
        val subject = args.subject
        val message = getHtml(args)
        emailSendProvider.sendEmail("FEW Letter <$from>", to, subject, message)
    }

    abstract fun getHtml(args: T): String
}
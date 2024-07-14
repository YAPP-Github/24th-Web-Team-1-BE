package com.few.email.sender

import com.few.email.sender.dto.SendMailArgs
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper

abstract class SendEmailSender<T : SendMailArgs<*, *>>(
    private val mailProperties: MailProperties,
    private val emailSender: JavaMailSender,
) {
    companion object {
        private const val UTF_8 = "utf-8"
    }

    fun send(args: T) {
        val to: String = args.to
        val message: MimeMessage = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, UTF_8)
        try {
            helper.setFrom(mailProperties.username)
            helper.setTo(to)
            helper.setSubject(args.subject)
            val html = getHtml(args)
            helper.setText(html, true)
        } catch (e: MessagingException) {
            throw RuntimeException(e)
        }
        emailSender.send(message)
    }

    abstract fun getHtml(args: T): String
}
package com.few.email.sender.provider

import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Profile("!prd")
@Component
class JavaEmailSendProvider(
    private val emailSender: JavaMailSender,
) : EmailSendProvider {
    companion object {
        private const val UTF_8 = "utf-8"
    }
    override fun sendEmail(form: String, to: String, subject: String, message: String) {
        val sendMessage: MimeMessage = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(sendMessage, UTF_8)
        try {
            helper.setFrom(form)
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(message, true)
        } catch (e: MessagingException) {
            throw RuntimeException(e)
        }
        emailSender.send(sendMessage)
    }
}
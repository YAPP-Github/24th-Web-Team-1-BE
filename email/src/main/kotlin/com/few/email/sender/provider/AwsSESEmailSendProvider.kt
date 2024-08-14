package com.few.email.sender.provider

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("prd")
@Component
class AwsSESEmailSendProvider(
    private val amazonSimpleEmailService: AmazonSimpleEmailService,
) : EmailSendProvider {
    companion object {
        private const val UTF_8 = "utf-8"
    }
    override fun sendEmail(form: String, to: String, subject: String, message: String) {
        val destination = Destination().withToAddresses(to)
        val sendMessage = Message()
            .withSubject(Content().withCharset(UTF_8).withData(subject))
            .withBody(Body().withHtml(Content().withCharset(UTF_8).withData(message)))

        val sendEmailRequest = SendEmailRequest()
            .withSource(form)
            .withDestination(destination)
            .withMessage(sendMessage)
            .withConfigurationSetName("few-configuration-set")

        amazonSimpleEmailService.sendEmail(sendEmailRequest)
    }
}
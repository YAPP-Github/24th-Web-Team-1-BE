package com.few.email.sender.provider

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Primary
@Profile("prd")
@Component
class AwsSESEmailSendProvider(
    private val amazonSimpleEmailService: AmazonSimpleEmailService,
    private val javaEmailSendProvider: JavaEmailSendProvider,
) : EmailSendProvider {
    private val log = KotlinLogging.logger {}
    companion object {
        private const val UTF_8 = "utf-8"
    }

    override fun sendEmail(from: String, to: String, subject: String, message: String) {
        val destination = Destination().withToAddresses(to)
        val sendMessage = Message()
            .withSubject(Content().withCharset(UTF_8).withData(subject))
            .withBody(Body().withHtml(Content().withCharset(UTF_8).withData(message)))

        val sendEmailRequest = SendEmailRequest()
            .withSource(from)
            .withDestination(destination)
            .withMessage(sendMessage)
            .withConfigurationSetName("few-configuration-set")

        runCatching {
            amazonSimpleEmailService.sendEmail(sendEmailRequest)
        }.onFailure {
            log.warn {
                "Failed to send email using AWS SES. Falling back to JavaMailSender. Error: $it"
            }
            runCatching {
                log.info {
                    "Sending email using JavaMailSender."
                }
                javaEmailSendProvider.sendEmail(from, to, subject, message)
            }.onFailure {
                log.error {
                    "Failed to send email using JavaMailSender."
                }
                throw it
            }
        }
    }
}
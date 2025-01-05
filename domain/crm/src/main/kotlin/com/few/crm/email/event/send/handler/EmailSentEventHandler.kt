package com.few.crm.email.event.send.handler

import com.few.crm.email.domain.EmailSendEventType
import com.few.crm.email.domain.EmailSendHistory
import com.few.crm.email.event.send.EmailSentEvent
import com.few.crm.email.repository.EmailSendHistoryRepository
import event.EventHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class EmailSentEventHandler(
    private val emailSendHistoryRepository: EmailSendHistoryRepository,
) : EventHandler<EmailSentEvent> {
    val logger = KotlinLogging.logger {}

    override fun handle(event: EmailSentEvent) {
        logger.info { "Handling EmailSentEvent: $event" }
        emailSendHistoryRepository.save(
            EmailSendHistory(
                userExternalId = event.userExternalId,
                userEmail = event.destination,
                emailMessageId = event.messageId,
                emailBody = event.emailBody,
                sendStatus = EmailSendEventType.SEND.name,
            ),
        )
    }
}
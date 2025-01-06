package com.few.crm.email.event.send.handler

import com.few.crm.email.event.send.EmailDeliveryEvent
import com.few.crm.email.repository.EmailSendHistoryRepository
import event.EventHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class EmailDeliveryEventHandler(
    private val emailSendHistoryRepository: EmailSendHistoryRepository,
) : EventHandler<EmailDeliveryEvent> {
    val log = KotlinLogging.logger {}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun handle(event: EmailDeliveryEvent) {
        log.info { "Handling EmailDeliveryEvent: $event" }
        // TODO check emailSendHistory and update status if history is not found retry 3 times
        emailSendHistoryRepository
            .findByEmailMessageId(event.messageId)
            ?.let {
                it.sendStatus = event.eventType.uppercase(Locale.getDefault())
                emailSendHistoryRepository.save(it)
            }
            ?: log.error { "EmailSendHistory not found for messageId: ${event.messageId}" }
    }
}
package com.few.crm.email.event.send.handler

import com.few.crm.email.event.send.EmailDeliveryEvent
import com.few.crm.email.repository.EmailSendHistoryRepository
import event.EventHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class EmailDeliveryEventHandler(
    private val emailSendHistoryRepository: EmailSendHistoryRepository,
) : EventHandler<EmailDeliveryEvent> {
    val log = KotlinLogging.logger {}

    override fun handle(event: EmailDeliveryEvent) {
        log.info { "Handling EmailDeliveryEvent: $event" }
        // TODO check emailSendHistory and update status if history is not found retry 3 times
    }
}
package com.few.crm.email.event.send.handler

import com.few.crm.email.event.send.EmailOpenEvent
import event.EventHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class EmailOpenEventHandler : EventHandler<EmailOpenEvent> {
    val logger = KotlinLogging.logger {}

    override fun handle(event: EmailOpenEvent) {
        logger.info { "Handling EmailOpenEvent: $event" }
        // TODO check emailSendHistory and update status if history is not found retry 3 times
    }
}
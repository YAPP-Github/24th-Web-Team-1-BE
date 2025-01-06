package com.few.crm.email.event.send

import com.few.crm.email.event.send.handler.*
import com.few.crm.email.relay.send.EmailSendEventMessageMapper
import event.isOutBox
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class EmailSendEventListener(
    private val emailSentEventHandler: EmailSentEventHandler,
    private val emailDeliveryEventHandler: EmailDeliveryEventHandler,
    private val emailOpenEventHandler: EmailOpenEventHandler,
    private val emailClickEventHandler: EmailClickEventHandler,
    private val emailDeliveryDelayEventHandler: EmailDeliveryDelayEventHandler,
    private val emailSendEventMessageMapper: EmailSendEventMessageMapper,
) {
    @Async
    @EventListener
    fun onEvent(event: EmailSendEvent) {
        when (event) {
            is EmailSentEvent -> emailSentEventHandler.handle(event)
            is EmailDeliveryEvent -> emailDeliveryEventHandler.handle(event)
            is EmailOpenEvent -> emailOpenEventHandler.handle(event)
            is EmailClickEvent -> emailClickEventHandler.handle(event)
            is EmailDeliveryDelayEvent -> emailDeliveryDelayEventHandler.handle(event)
        }
    }

    fun relay(event: EmailSendEvent) {
        if (event.isOutBox()) {
            emailSendEventMessageMapper.map(event).ifPresent { message ->
                // TODO Relay message
            }
        }
    }
}
package com.few.crm.email.relay.send

import com.few.crm.email.event.send.*
import event.message.MessageMapper
import event.message.MessagePayload
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class EmailSendEventMessageMapper : MessageMapper<EmailSendEvent, EmailSendMessage>() {
    override fun map(event: EmailSendEvent): Optional<EmailSendMessage> = Optional.empty()

    fun to(messagePayload: MessagePayload): Optional<EmailSendEvent> =
        when (messagePayload.eventType!!.lowercase(Locale.getDefault())) {
            "open" ->
                Optional.of(
                    EmailOpenEvent(
                        eventId = messagePayload.eventId!!,
                        eventType = messagePayload.eventType!!,
                        eventTime = messagePayload.eventTime!!,
                        messageId = messagePayload.data!!["messageId"] as String,
                        destination = messagePayload.data!!["destination"] as String,
                        timestamp = messagePayload.data!!["timestamp"] as LocalDateTime,
                    ),
                )
            "delivery" ->
                Optional.of(
                    EmailDeliveryEvent(
                        eventId = messagePayload.eventId!!,
                        eventType = messagePayload.eventType!!,
                        eventTime = messagePayload.eventTime!!,
                        messageId = messagePayload.data!!["messageId"] as String,
                        destination = messagePayload.data!!["destination"] as String,
                        timestamp = messagePayload.data!!["timestamp"] as LocalDateTime,
                    ),
                )
            "click" ->
                Optional.of(
                    EmailClickEvent(
                        eventId = messagePayload.eventId!!,
                        eventType = messagePayload.eventType!!,
                        eventTime = messagePayload.eventTime!!,
                        messageId = messagePayload.data!!["messageId"] as String,
                        destination = messagePayload.data!!["destination"] as String,
                        timestamp = messagePayload.data!!["timestamp"] as LocalDateTime,
                    ),
                )
            "deliverydelay" ->
                Optional.of(
                    EmailDeliveryDelayEvent(
                        eventId = messagePayload.eventId!!,
                        eventType = messagePayload.eventType!!,
                        eventTime = messagePayload.eventTime!!,
                        messageId = messagePayload.data!!["messageId"] as String,
                        destination = messagePayload.data!!["destination"] as String,
                        timestamp = messagePayload.data!!["timestamp"] as LocalDateTime,
                    ),
                )
            else -> Optional.empty()
        }
}
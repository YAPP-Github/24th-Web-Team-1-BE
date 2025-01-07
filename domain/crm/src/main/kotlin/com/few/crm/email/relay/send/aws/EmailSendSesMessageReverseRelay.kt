package com.few.crm.email.relay.send.aws

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.few.crm.config.CrmSqsConfig.Companion.SQS_LISTENER_CONTAINER_FACTORY
import com.few.crm.email.event.send.EmailSendEvent
import com.few.crm.email.relay.send.EmailSendEventMessageMapper
import com.few.crm.email.relay.send.EmailSendMessageReverseRelay
import event.EventUtils
import event.message.MessagePayload
import io.awspring.cloud.sqs.annotation.SqsListener
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZonedDateTime

fun ObjectMapper.readMail(message: String): Map<String, Any> =
    (readValue(message) as Map<String, Any>).let { it["Message"] as String }.let { readValue(it) as Map<String, Any> }

fun Map<*, *>.messageId() = this["messageId"] as String

fun Map<*, *>.timestamp(): LocalDateTime = (this["timestamp"] as String).let { ZonedDateTime.parse(it).toLocalDateTime() }

fun Map<*, *>.destination() = (this["destination"] as List<*>).joinToString(", ")

@Profile("!local")
@Service
class EmailSendSesMessageReverseRelay(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val objectMapper: ObjectMapper,
    private val eventMessageMapper: EmailSendEventMessageMapper,
) : EmailSendMessageReverseRelay() {
    val log = KotlinLogging.logger { }

    @SqsListener(queueNames = ["few_crm_sqs"], factory = SQS_LISTENER_CONTAINER_FACTORY)
    fun onMessage(
        message: String,
        acknowledgement: Acknowledgement,
    ) {
        objectMapper
            .readMail(message)
            .let {
                val mail = it["mail"] as Map<*, *>
                MessagePayload(
                    eventId = EventUtils.generateEventId(),
                    eventType = it["eventType"] as String,
                    eventTime = LocalDateTime.now(),
                    data =
                        mapOf(
                            "messageId" to mail.messageId(),
                            "destination" to mail.destination(),
                            "timestamp" to mail.timestamp(),
                        ),
                )
            }.let {
                eventMessageMapper.to(it)
            }.ifPresent {
                publish(it)
            }
        acknowledgement.acknowledge()
    }

    override fun publish(event: EmailSendEvent) {
        log.info { "Publishing event: $event" }
        applicationEventPublisher.publishEvent(event)
    }
}
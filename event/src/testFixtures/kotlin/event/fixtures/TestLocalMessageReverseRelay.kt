package event.fixtures

import event.message.MessageReverseRelay
import event.message.local.LocalSubscribeMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener

class TestLocalMessageReverseRelay : MessageReverseRelay<TestEvent> {
    val log = KotlinLogging.logger { }

    @EventListener
    @LocalSubscribeMessage(topic = "test")
    fun onApplicationEvent(message: TestReverseMessage) {
        log.info { "Receive Message: $message" }
        publish(
            TestEvent(
                eventId = message.payload?.eventId!!,
                eventType = message.payload?.eventType!!,
                eventTime = message.payload?.eventTime!!,
            ),
        )
    }

    override fun publish(event: TestEvent) {
        log.info { "Publish Event: $event" }
    }
}
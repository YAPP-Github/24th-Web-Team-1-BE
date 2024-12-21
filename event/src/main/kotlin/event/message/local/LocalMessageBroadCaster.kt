package event.message.local

import com.fasterxml.jackson.databind.ObjectMapper
import event.message.Message
import event.message.MessageReverseRelay
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import java.lang.reflect.Method

fun Method.isForLocalMessageReverseRelay(): Boolean = this.annotations.any { annotation -> annotation is LocalSubscribeMessage }

fun Method.messageType(): Class<*> = this.parameterTypes[0]

@Suppress("LABEL_NAME_CLASH")
open class LocalMessageBroadCaster(
    private val objectMapper: ObjectMapper,
    private val messageReverseRelays: List<MessageReverseRelay<*>>,
) {
    private val log = KotlinLogging.logger { }

    @Async
    @EventListener
    open fun onApplicationEvent(message: Message) {
        log.info { "[${Thread.currentThread().name}] Receive message: $message" }
        messageReverseRelays.forEach { relay ->
            relay.javaClass.methods
                .filter { it.name.equals("onApplicationEvent") && it.isForLocalMessageReverseRelay() }
                .forEach { method ->
                    val messageType = method.messageType()
                    method.getAnnotation(LocalSubscribeMessage::class.java).topic.let {
                        if (!message.javaClass.name.contains(it, ignoreCase = true)) {
                            return@forEach
                        }
                    }
                    objectMapper.convertValue(message.payload, messageType).let { relayMessage ->
                        log.info { "[${Thread.currentThread().name}] Publish message to ${relay.javaClass.simpleName}: $relayMessage" }
                        method.invoke(relay, relayMessage)
                    }
                }
        }
    }
}
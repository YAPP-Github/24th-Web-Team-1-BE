package event.fixtures

import event.message.MessageSender
import org.springframework.context.ApplicationEventPublisher

class TestLocalMessageSender(
    private val applicationEventPublisher: ApplicationEventPublisher,
) : MessageSender<TestMessage> {
    override fun send(message: TestMessage) {
        applicationEventPublisher.publishEvent(message)
    }
}
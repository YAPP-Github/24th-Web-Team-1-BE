package event.fixtures

import event.message.MessageRelay

class TestLocalMessageRelay(
    private val messageSender: TestLocalMessageSender,
) : MessageRelay<TestMessage> {
    override fun publish(message: TestMessage) {
        messageSender.send(message)
    }
}
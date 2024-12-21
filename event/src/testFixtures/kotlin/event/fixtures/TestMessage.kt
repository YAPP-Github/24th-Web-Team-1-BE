package event.fixtures

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import event.message.Message
import event.message.MessagePayload

class TestMessage(
    payload: MessagePayload?,
) : Message(payload) {
    @JsonCreator
    constructor(
        @JsonProperty("eventId") eventId: String?,
        @JsonProperty("eventType") eventType: String?,
        @JsonProperty("eventTime") eventTime: Long?,
        @JsonProperty("data") data: Map<String, Any>?,
    ) : this(
        MessagePayload(
            eventId,
            eventType,
            eventTime,
            data,
        ),
    )

    override fun toString(): String = "TestMessage(payload=$payload)"
}
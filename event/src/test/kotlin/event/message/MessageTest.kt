package event.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import event.EventUtils
import event.fixtures.TestMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MessageTest {
    companion object {
        val objectMapper =
            ObjectMapper().apply {
                registerKotlinModule()
            }
    }

    @Test
    @DisplayName("Message 객체를 JSON으로 변환할 수 있다.")
    fun to_json() {
        // given
        val message =
            TestMessage(
                MessagePayload(
                    eventId = EventUtils.generateEventId(),
                    eventType = "Test",
                    eventTime = System.currentTimeMillis(),
                    data =
                        mapOf(
                            "test" to "test",
                        ),
                ),
            )

        // when
        val json = objectMapper.writeValueAsString(message)

        // Then
        val jsonTree = objectMapper.readTree(json)
        val compareTree =
            objectMapper.readTree(
                """
                {
                    "payload": {
                        "eventId": "${message.payload?.eventId}",
                        "eventType": "${message.payload?.eventType}",
                        "eventTime": ${message.payload?.eventTime},
                        "data": ${objectMapper.writeValueAsString(message.payload?.data)}
                    }
                }
                """.trimIndent(),
            )
        assertTrue(jsonTree.equals(compareTree))
    }

    @Test
    @DisplayName("JSON을 ObjectMapper의 readValue 메서드로 Message 객체로 변환할 수 있다.")
    fun json_to_message_by_readValue() {
        // given
        val eventId = EventUtils.generateEventId()
        val eventTime = System.currentTimeMillis()
        val json =
            """
            {
                "payload": {
                    "eventId": "$eventId",
                    "eventType": "Test",
                    "eventTime": $eventTime,
                    "data": {
                        "test": "test"
                    }
                }
            }
            """.trimIndent()

        // when
        val message = objectMapper.readValue(json, TestMessage::class.java)
        val compare =
            TestMessage(
                MessagePayload(
                    eventId = eventId,
                    eventType = "Test",
                    eventTime = eventTime,
                    data = mapOf("test" to "test"),
                ),
            )

        // Then
        assertEquals(compare, message)
    }

    @Test
    @DisplayName("MessagePayload 객체가 있으면 ObjectMapper의 convertValue 메서드로 Message 객체로 변환할 수 있다.")
    fun message_payload_to_message_by_convertValue() {
        // given
        val eventId = EventUtils.generateEventId()
        val eventTime = System.currentTimeMillis()
        val messagePayload =
            MessagePayload(
                eventId = eventId,
                eventType = "Test",
                eventTime = eventTime,
                data = mapOf("test" to "test"),
            )

        // when
        val message = objectMapper.convertValue(messagePayload, TestMessage::class.java)
        val compare =
            TestMessage(
                MessagePayload(
                    eventId = eventId,
                    eventType = "Test",
                    eventTime = eventTime,
                    data = mapOf("test" to "test"),
                ),
            )

        // Then
        assertEquals(compare, message)
    }
}
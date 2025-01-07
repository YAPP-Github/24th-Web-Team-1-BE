package event.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import event.EventUtils
import event.fixtures.TestMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessageTest {
    private val objectMapper =
        ObjectMapper().apply {
            registerKotlinModule()
            registerModules(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            val localDateTimeDeserializer = LocalDateTimeDeserializer(dateTimeFormatter)
            val module = SimpleModule().addDeserializer(LocalDateTime::class.java, localDateTimeDeserializer)
            registerModule(module)
        }

    @Disabled
    @Test
    @DisplayName("Message 객체를 JSON으로 변환할 수 있다.")
    fun to_json() {
        // given
        val message =
            TestMessage(
                MessagePayload(
                    eventId = EventUtils.generateEventId(),
                    eventType = "Test",
                    eventTime = LocalDateTime.now(),
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
                        "eventTime": "${message.payload?.eventTime}",
                        "data": ${objectMapper.writeValueAsString(message.payload?.data)}
                    }
                }
                """.trimIndent(),
            )
        assertTrue(jsonTree == compareTree)
    }

    @Disabled
    @Test
    @DisplayName("MessagePayload 객체가 있으면 ObjectMapper의 convertValue 메서드로 Message 객체로 변환할 수 있다.")
    fun message_payload_to_message_by_convertValue() {
        // given
        val eventId = EventUtils.generateEventId()
        val eventTime = LocalDateTime.now()
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
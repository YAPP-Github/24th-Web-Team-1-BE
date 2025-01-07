package event.message.local

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import event.EventUtils
import event.fixtures.TestLocalMessageReverseRelay
import event.fixtures.TestMessage
import event.message.MessagePayload
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Profile("test, local")
@SpringJUnitConfig(LocalMessageBroadCasterTest.LocalMessageBroadCasterTestConfig::class, LocalMessageConfig::class)
class LocalMessageBroadCasterTest {
    private val log = KotlinLogging.logger { }

    @TestConfiguration
    class LocalMessageBroadCasterTestConfig {
        @Bean
        fun testLocalMessageReverseRelay(): TestLocalMessageReverseRelay = TestLocalMessageReverseRelay()

        @Bean
        fun objectMapper(): ObjectMapper =
            ObjectMapper().apply {
                registerKotlinModule()
                registerModules(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

                val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                val localDateTimeDeserializer = LocalDateTimeDeserializer(dateTimeFormatter)
                val module = SimpleModule().addDeserializer(LocalDateTime::class.java, localDateTimeDeserializer)
                registerModule(module)
            }
    }

    @Autowired
    lateinit var context: ApplicationContext

    @Disabled
    @Test
    fun is_registered_localMessageBroadCaster_bean() {
        // given & when
        val bean = context.getBean(LocalMessageBroadCaster::class.java)

        // then
        assertNotNull(bean)
    }

    @Disabled
    @Test
    fun localMessageBroadCaster_broadcast_message() {
        val originalOut = System.out
        var output: String? = null
        try {
            val outputStream = ByteArrayOutputStream()
            System.setOut(PrintStream(outputStream))

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
            val broadcaster = context.getBean(LocalMessageBroadCaster::class.java)

            // when
            broadcaster.onApplicationEvent(message)

            // then
            output = outputStream.toString()
            val isMatch =
                output.split("\n").stream().anyMatch {
                    it.contains(message.toString(), ignoreCase = true) &&
                        it.contains(
                            "MessageReverseRelay",
                            ignoreCase = true,
                        )
                }
            assertTrue(isMatch)
        } finally {
            System.setOut(originalOut)
            log.info { output }
        }
    }
}
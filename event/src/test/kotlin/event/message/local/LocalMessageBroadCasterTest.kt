package event.message.local

import event.EventUtils
import event.fixtures.TestLocalMessageReverseRelay
import event.fixtures.TestMessage
import event.message.MessagePayload
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import java.io.ByteArrayOutputStream
import java.io.PrintStream

@Profile("test, local")
@SpringJUnitConfig(LocalMessageBroadCasterTest.LocalMessageBroadCasterTestConfig::class, LocalMessageConfig::class)
class LocalMessageBroadCasterTest {
    private val log = KotlinLogging.logger { }

    @TestConfiguration
    class LocalMessageBroadCasterTestConfig {
        @Bean
        fun testLocalMessageReverseRelay(): TestLocalMessageReverseRelay = TestLocalMessageReverseRelay()
    }

    @Autowired
    lateinit var context: ApplicationContext

    @Test
    fun is_registered_localMessageBroadCaster_bean() {
        // given & when
        val bean = context.getBean(LocalMessageBroadCaster::class.java)

        // then
        assertNotNull(bean)
    }

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
                        eventTime = System.currentTimeMillis(),
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
            assertTrue(output.contains(message.toString()))
        } finally {
            System.setOut(originalOut)
            log.info { output }
        }
    }
}
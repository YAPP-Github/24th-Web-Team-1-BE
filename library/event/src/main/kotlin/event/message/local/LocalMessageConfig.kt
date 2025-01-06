package event.message.local

import com.fasterxml.jackson.databind.ObjectMapper
import event.message.MessageReverseRelay
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class LocalMessageConfig {
    val log = KotlinLogging.logger { }

    @Profile("local")
    @Bean
    fun localMessageBroadCaster(
        context: ApplicationContext,
        objectMapper: ObjectMapper,
        messageReverseRelays: List<MessageReverseRelay<*>>,
    ): LocalMessageBroadCaster? {
        context.beanDefinitionNames
            .filter {
                it.contains("Local", ignoreCase = true) &&
                    it.contains(
                        "MessageReverseRelay",
                        ignoreCase = true,
                    )
            }.takeIf { it.isNotEmpty() }
            ?.also { localReverseBeanNames ->
                log.info { "LocalReverse Beans are found: ${localReverseBeanNames.joinToString(", ")}" }
                log.info { "Local message broadcaster is registered" }
                return LocalMessageBroadCaster(objectMapper, messageReverseRelays)
            }
        return null
    }
}
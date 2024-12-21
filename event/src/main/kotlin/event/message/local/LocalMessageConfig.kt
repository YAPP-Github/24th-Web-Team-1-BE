package event.message.local

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import event.message.MessageReverseRelay
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@Configuration
class LocalMessageConfig {
    val log = KotlinLogging.logger { }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper::class)
    fun objectMapper(): ObjectMapper =
        ObjectMapper().apply {
            registerKotlinModule()
        }

    @Bean
    fun localMessageBeanPostProcessor(
        objectMapper: ObjectMapper,
        messageReverseRelays: List<MessageReverseRelay<*>>,
    ): BeanFactoryPostProcessor =
        BeanFactoryPostProcessor { beanFactory ->
            beanFactory.beanDefinitionNames
                .filter {
                    it.contains("Local", ignoreCase = true) &&
                        it.contains(
                            "MessageReverseRelay",
                            ignoreCase = true,
                        )
                }.takeIf { it.isNotEmpty() }
                ?.also { localReverseBeanNames ->
                    log.info { "LocalReverse Beans are found: ${localReverseBeanNames.joinToString(", ")}" }
                    beanFactory.registerSingleton(
                        "localMessageBroadCaster",
                        LocalMessageBroadCaster(objectMapper, messageReverseRelays),
                    )
                    log.info { "Local message broadcaster is registered" }
                }
        }
}
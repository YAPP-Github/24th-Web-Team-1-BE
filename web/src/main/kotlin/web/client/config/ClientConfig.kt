package web.client.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import web.config.WebConfig
import java.time.Duration

@Configuration
class ClientConfig {

    companion object {
        const val REST_TEMPLATE = WebConfig.BEAN_NAME_PREFIX + "RestTemplate"
    }

    @Bean(name = [REST_TEMPLATE])
    fun restTemplate(
        restTemplateBuilder: RestTemplateBuilder,
        @Value("\${web.client.timeout.connect}") connectTimeout: Int,
        @Value("\${web.client.timeout.read}") readTimeout: Int,
    ): RestTemplate {
        return restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(connectTimeout.toLong()))
            .setReadTimeout(Duration.ofSeconds(readTimeout.toLong()))
            .build()
    }
}